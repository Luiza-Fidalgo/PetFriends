package com.petfriends.almoxarifado;

import com.petfriends.almoxarifado.domain.ItemEstoque;
import com.petfriends.almoxarifado.domain.ItemEstoqueRepository;
import com.petfriends.almoxarifado.domain.Quantidade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AlmoxarifadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmoxarifadoApplication.class, args);
    }

    /**
     * Roda 1x quando a aplicacao sobe: coloca alguns produtos no estoque
     * para a gente conseguir testar o fluxo.
     */
    @Bean
    CommandLineRunner popularEstoque(ItemEstoqueRepository repo) {
        return args -> {
            repo.save(new ItemEstoque("RACAO-001", "Racao Premium Caes", new Quantidade(10)));
            repo.save(new ItemEstoque("BRINQ-002", "Bolinha Mordedor", new Quantidade(5)));
            System.out.println("🗄️ [ALMOXARIFADO] Estoque inicial carregado (RACAO-001=10, BRINQ-002=5)");
        };
    }
}
