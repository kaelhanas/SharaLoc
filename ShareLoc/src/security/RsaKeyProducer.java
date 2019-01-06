package security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;

/**
 * Classe permettant la g�n�ration d'une cl� RSA sur 2048 bits
 */
public class RsaKeyProducer {

	// cl� RSA
	private static RsaJsonWebKey theOne;

	/**
	 * M�thode produisant une nouvelle cl� si elle n'existe pas encore
	 * 
	 * @return la cl� g�n�r�e
	 */
	public static RsaJsonWebKey produce() {
		if (theOne == null) {
			try {
				theOne = RsaJwkGenerator.generateJwk(2048);
			} catch (JoseException ex) {
				Logger.getLogger(RsaKeyProducer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		System.out.println("RSA Key setup... " + theOne);
		return theOne;
	}
}