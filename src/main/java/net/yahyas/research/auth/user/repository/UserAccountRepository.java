package net.yahyas.research.auth.user.repository;

import java.util.Optional;

import net.yahyas.research.auth.user.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	boolean existsByEmail(String email);

	Optional<UserAccount> findByEmail(String email);
}
