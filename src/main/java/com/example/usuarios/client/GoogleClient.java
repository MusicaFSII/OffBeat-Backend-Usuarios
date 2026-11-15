package com.example.usuarios.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.usuarios.dto.GoogleTokenInfoDTO;
import com.example.usuarios.exception.CredencialesInvalidasException;

@Component
public class GoogleClient {

    private final RestClient restClient = RestClient.create("https://oauth2.googleapis.com");
    private final String clientId;

    public GoogleClient(@Value("${google.client.id}") String clientId){
        this.clientId = clientId;
    }

    //valida el token con Google y devuelve el correo de la cuenta
    public String obtenerCorreoVerificado(String credential){
        GoogleTokenInfoDTO info;
        try {
            info = restClient.get()
                .uri("/tokeninfo?id_token={token}", credential)
                .retrieve()
                .body(GoogleTokenInfoDTO.class);
        } catch (RestClientException e) {
            //token invalido, vencido, o Google no responde
            throw new CredencialesInvalidasException("No se pudo validar la cuenta de Google");
        }

        if(info == null
            || info.getEmail() == null
            || !clientId.equals(info.getAud())
            || !"true".equals(info.getEmailVerified())){
            throw new CredencialesInvalidasException("La cuenta de Google no es valida");
        }
        return info.getEmail();
    }
}