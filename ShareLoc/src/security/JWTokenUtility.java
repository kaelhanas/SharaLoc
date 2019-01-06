package security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

public class JWTokenUtility {

	public static String buildJWT(String subject) { /*subject = ID utilisateur*/
		RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();
		System.out.println("RSA hash code... " + rsaJsonWebKey.hashCode());

		// création de la "charge utile" ou payload - la donnée à  crypter, ici
		// 'subject'
		JwtClaims claims = new JwtClaims();
		claims.setSubject(subject);

		// création de la signature
		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setKey(rsaJsonWebKey.getPrivateKey());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// encodage du token JWT
		String jwt = null;
		try {
			jwt = jws.getCompactSerialization();
		} catch (JoseException ex) {
			Logger.getLogger(JWTAuthFilter.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println("Claim:\n" + claims);
		System.out.println("JWS:\n" + jws);
		System.out.println("JWT:\n" + jwt);

		return jwt;
	}
}