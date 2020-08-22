package co.com.udem.inmobiliariaclient.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import co.com.udem.inmobiliariaclient.entities.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
	
	@Query("SELECT u.token FROM UserToken u WHERE u.username = ?1")
    public String obtenerToken(String username);
	
	@Query("SELECT u.username FROM UserToken u WHERE u.username = ?1")
    public String findByUsername(String username);

	@Transactional
    @Modifying
    @Query("UPDATE UserToken u SET u.token = ?1 WHERE u.username = ?2")
    void updateToken(String token, String username);
}
