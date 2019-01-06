package security;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import api.Authentification;

@Provider
@SigninNeeded
public class JWTAuthFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null)
			throw new NotAuthorizedException("Bearer");

		// r�cup�ration du JWT et v�rification de la signature
		if (authHeader.startsWith("Bearer")) {
			try {
				// test de validation de la signature et d�codage du contenu du token
				final String subject = validate(authHeader.split(" ")[1]);

				// d�finition du contexte de s�curit�
				final SecurityContext securityContext = requestContext.getSecurityContext();
				if (subject != null) {
					requestContext.setSecurityContext(new SecurityContext() {
						@Override
						public Principal getUserPrincipal() {
							return new Principal() {
								@Override
								public String getName() {
									return subject;
								}
							};
						}

						@Override
						public boolean isUserInRole(String role) {
							List<String> roles = Authentification.findUserRoles(subject);
							if (roles != null)
								return roles.contains(role);
							return false;
						}

						@Override
						public boolean isSecure() {
							return securityContext.isSecure();
						}

						@Override
						public String getAuthenticationScheme() {
							return securityContext.getAuthenticationScheme();
						}
					});
				}
			} catch (InvalidJwtException ex) {
				requestContext.setProperty("auth-failed", true);
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}

		} else {
			requestContext.setProperty("auth-failed", true);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	/**
	 * M�thode de validation d'un JWT
	 * 
	 * @param jwt le token � tester
	 * @return le contenu du token d�cryt� s'il est valide, null sinon
	 * @throws InvalidJwtException si le token n'est pas valide
	 */
	private String validate(String jwt) throws InvalidJwtException {
		String subject = null;
		RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

		// construction du d�codeur de JWT
		JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireSubject()
				.setVerificationKey(rsaJsonWebKey.getKey()).build();

		// validation du JWT et r�cup�ration du contenu
		JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
		subject = (String) jwtClaims.getClaimValue("sub");

		return subject;
	}
}
