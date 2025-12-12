package com.app.movie;

import com.app.movie.Models.Role;
import com.app.movie.Repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRoleIfNotExists("USER");
        createRoleIfNotExists("ADMIN");
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).build()
                ));
    }
}