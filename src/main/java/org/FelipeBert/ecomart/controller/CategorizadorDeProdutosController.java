package org.FelipeBert.ecomart.controller;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorizador")
public class CategorizadorDeProdutosController {

    private final ChatClient chatClient;

    public CategorizadorDeProdutosController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultOptions(ChatOptionsBuilder.builder()
                        .withModel("gpt-4o-mini")
                        .build())
                .build();
    }

    @GetMapping
    public String categorizarProduto(String produto){
        String system = """
            Você é um categorizador de produtos e deve responder apenas o nome da categoria do produto informado
           
            Escolha uma categoria dentra a lista abaixo:
           
            1. Higiene pessoal
            2. Eletronicos
            3. Esportes
            4. Outros
           
            ###### exemplo de uso:
           
            Pergunta: Bola de futebol
            Resposta: Esportes
            """;

        var tokens = contarTokens(system, produto);
        System.out.println("Quantidade de Tokens: " + tokens);

        return this.chatClient.prompt()
                .system(system)
                .user(produto)
                .options(ChatOptionsBuilder.builder()
                        .withTemperature(0.85)
                        .build())
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }

    private int contarTokens(String system, String user){
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_4O_MINI);
        return enc.countTokens(system + user);
    }
}
