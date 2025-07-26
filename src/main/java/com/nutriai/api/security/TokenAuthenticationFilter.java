package com.nutriai.api.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Filtro de segurança que intercepta todas as requisições para validar o token JWT do Firebase.
 * Se o token for válido, o usuário é autenticado no contexto de segurança do Spring.
 */

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    // Dependência para verificar o token com o Firebase.
    private final FirebaseAuth firebaseAuth;
    // Dependência para converter o objeto de erro em JSON.
    private final ObjectMapper objectMapper;


    public TokenAuthenticationFilter(FirebaseAuth firebaseAuth, ObjectMapper objectMapper) {
        this.firebaseAuth = firebaseAuth;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)){
            String token = authorizationHeader.replace(BEARER_PREFIX, "");

            Optional<FirebaseToken> decodedToken = verifyToken(token);

            if (decodedToken.isPresent()){
                String userId = decodedToken.get().getUid();
                // ...Cria um objeto de autenticação para o Spring Security.
                // O primeiro parâmetro (principal) é quem o usuário é (ID).
                // O segundo (credentials) é nulo, pois já validamos com o token.
                // O terceiro (authorities) são as permissões/roles, que por enquanto está vazia.
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                // ...Define o usuário como autenticado no contexto de segurança.
                // A partir daqui, a aplicação "sabe" quem está logado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                setAuthErrorDetails(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para verificar a validade do token com a SDK do Firebase.
     * @param token O JWT extraído do cabeçalho.
     * @return Um Optional contendo o FirebaseToken decodificado se for válido, ou vazio se for inválido.
     */

    private Optional<FirebaseToken> verifyToken(String token){
        try {
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, true);
            return Optional.of(firebaseToken);
        } catch (FirebaseAuthException e) {
            return Optional.empty();
        }
    }


    /**
     * Configura uma resposta de erro padronizada para falhas de autenticação.
     */

    private void setAuthErrorDetails(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "A autenticação falhou: o token está ausente, é inválido ou expirou."
        );
        problemDetail.setTitle("Usuário Não Autorizado");

        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }


}
