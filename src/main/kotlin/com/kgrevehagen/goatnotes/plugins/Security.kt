package com.kgrevehagen.goatnotes.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import java.net.URI
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val jwksUrl = URI.create(environment.config.property("jwt.jwksUrl").getString()).toURL()

    val jwkProvider = JwkProviderBuilder(jwksUrl)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    authentication {
        jwt {
            realm = jwtRealm
            verifier(jwkProvider, issuer)
            validate { credential ->
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        jwt("oauth-jwt") {
            realm = jwtRealm
            verifier(jwkProvider, issuer)
            authHeader { call ->
                val token = call.request.cookies["access_token"]
                if (token != null) HttpAuthHeader.Single("Bearer", token) else null
            }
            validate { credential ->
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ -> }
        }

        oauth("oauth") {
            client = HttpClient(CIO)
            urlProvider = { this@configureSecurity.environment.config.property("oauth.redirectUrl").getString() }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "oauth2",
                    requestMethod = HttpMethod.Post,
                    authorizeUrl = this@configureSecurity.environment.config.property("oauth.authUrl").getString(),
                    accessTokenUrl = this@configureSecurity.environment.config.property("oauth.tokenUrl").getString(),
                    clientId = this@configureSecurity.environment.config.property("oauth.clientId").getString(),
                    clientSecret = this@configureSecurity.environment.config.property("oauth.clientSecret").getString(),
                    defaultScopes = listOf(
                        this@configureSecurity.environment.config.property("oauth.scopes").getString()
                    ),
                    onStateCreated = { call, _ ->
                        val originalUri = call.request.uri
                        call.response.cookies.append(
                            Cookie(name = "origin_uri", value = originalUri, path = "/", httpOnly = true, maxAge = 60)
                        )
                    }
                )
            }
        }
    }
}
