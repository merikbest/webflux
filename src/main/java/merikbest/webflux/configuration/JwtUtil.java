package merikbest.webflux.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import merikbest.webflux.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret; // ключ шифрования

    @Value("${jwt.expiration}")
    private String expirationTime; // время "жизни" токена

    // метод расшифровки токена
    public String extractUsername(String authToken) {
        return getClaimsFromToken(authToken)
                .getSubject();
    }

    // метод проверки токена пользователя
    public boolean validateToken(String authToken) {
        return getClaimsFromToken(authToken)
                .getExpiration()
                .before(new Date());
    }

    // вспомогательный метод для расшифровки токена
    public Claims getClaimsFromToken(String authToken) {
        String key = Base64.getEncoder().encodeToString(secret.getBytes()); // key - расшифруем получение сообщение
        // передаем key в parserBuilder и передаем в parseClaimsJws токен authToken который получили от пользователя
        // .getBody() - получаем контент в расшифрованном  виде который содержался в токене
        // .getSubject() - извлекаем пользователя
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }

    // метод для генерации токена
    public String generateTokenUser(User user) {
        // в токене информация лежит в виде "словаря"
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", List.of(user.getRole()));

        // количество секунд "жизни" токена
        long expirationSeconds = Long.parseLong(expirationTime);
        // время создания токена
        Date creationDate = new Date();
        // время "окончания" токена
        Date expirationDate = new Date(creationDate.getTime() + expirationSeconds * 1000);

        // возвращаем новый токен
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // указываем юзера
                .setIssuedAt(creationDate)      // время создания токена
                .setExpiration(expirationDate)  // время "окончания" токена
                .signWith(Keys.hmacShaKeyFor(secret.getBytes())) // подпись с ключом
                .compact(); // получаем стринговый токен
    }
}
