package com.sapient.login.builder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JWTBuilderTest {

    private JWTBuilder builderUnderTest;

    @BeforeEach
    public void setUp() {
        builderUnderTest = new JWTBuilder();
        ReflectionTestUtils.setField(builderUnderTest, "privateKey", "MIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQCw64MdwAihA584JJvjq9pmYnc7x4dIa6ScofUOGLbr74uoE07BsQZli2f9Hny84lWq0UYFh3S4BrLmW2Ls/Oe4xyuf2GWlSuE2Pak2DnxSk1NnhWJrMX2yBtDO48x5vBgpnIxYIDvOFvU7Que9Fk1vBL4XZYFsHilW75hIEVflcDQ6yUzV3jwGnBlDgKdqsd42C1kJjHkDZcj0si3ADNtAGqVaGbPoi3Vu3Kmemb4mglcm6eH8lI47qX0BxKMK+7U1PRR+5bUyEulNLQLYmZf9KVpuDo7QYjzYT9zxexixmZW5cW0yhr8sf0kFqt0tBsIghMXXLyd3/5vVLXFhK6h9AgMBAAECggEAGzVtKcMh0Jl9ACZpLx977CkSi0gJXzLm0wv5Yxyb7Tce9E9b2l/+StmguYXZyzbH1AhxS2JkqfSlyNWfJjFS9dDzvZaLM1uLvUrIXj7mEdEEj7Qsya2H1jxEGr1f8gzibmvcDaWPbQeM2XtVpnfqmrtQ/MoomFcfJnacpeC88tUINJS37qw0xeLDEifBGoGfd6IyGTgA8imve9n6075uNcZoY3gBhTK90e1ZOuyn2v+MMbaOqbnnROAZjaUVQ7azxcfqwobQxBPuz7NZrhgNLL2YKoRNIdJm9dLmq5LGYPiKdJYYNs/cNLYCtdc1mR9T0mdcqk/wVNnQ0IcqOfrXJQKBgQDgwbw/ShlQPeEcXYdpoU7MucsjpEHcr1qKsmClBOOFassMbztVNl5+8BEu7k9E9MFmT9S1DEFz5i9UdKJhmCaKQsqbdeHXWpesaeZM5qynW2jYm/SziYIxvXvj2Tbs7vga5DqB2C3ZCHsm3/pHIgQBsnP7eIo/TxiCrjCJ5NXPawKBgQDJg3D1/agH2Rly+2PCd1qi6l2Tt+M0q1nP9iJTK8AOYTHgPfCbXq5sQUx6gQAPFa1aXimpy3SuUOvcQhaGAa3wVqfMNPD8x+TTY2Lrcz2M+FgeQKCIZUo+Wt0ArVlhb4AyIv+uS5aecwCrGrGkSJsmL8GedmcRimg2LM4MdqTptwKBgD3rQbhR6q1fjs/ouHCSXwopuVwlWXu514DUaamssh3EcRII5tiv9NKVwrYiMzZ1E37jSV9jc/jBFpL22vF2IFAA3J59mG1i6Nw4BJl1B2XH2heqx3xaysJ3SFVaNzaJE1MdQz0YWHEtU/g+xk2ogmARUMCb0cJlQQRnhrnGkFznAn8h2PIgX3rZ1p1vssoO3/1ZYARXy4teTWnIhI8/aRnZUHoQMFc5wa2KgW1XJlPcNMfpZeeZnZL7tJvpYzBlUeLle6izpDDWRL0qxIbZBYJRK2WhrzlHr7vIvXphorxgoVnnEfngTNH/y1FRbCpqbcehomPjOgAV8imO7/esIEGTAoGAA9EMNzIaJu345nHUctOiRG32UtbL/1k+y9BF/+R/8OgC6mJeiNa+FT9r1OVYeL9D+a8ppsESRgZx0MdtGKJ0toF6Xff34yrxbx48F3iJBrVuIV2QTGyI78QwzTRCCViZ4hJAeYK9wP5u6i6lX62901DsNhSIcB/ZJS2r41g1oIM=");
    }

    @Test
    void verify_ValidJWT() throws Exception {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(builderUnderTest.getSignedJWT("testUser@gmail.com"));

        assertEquals("testUser@gmail.com", claims.getBody().getSubject());
    }

    private PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsOuDHcAIoQOfOCSb46vaZmJ3O8eHSGuknKH1Dhi26++LqBNOwbEGZYtn/R58vOJVqtFGBYd0uAay5lti7PznuMcrn9hlpUrhNj2pNg58UpNTZ4ViazF9sgbQzuPMebwYKZyMWCA7zhb1O0LnvRZNbwS+F2WBbB4pVu+YSBFX5XA0OslM1d48BpwZQ4CnarHeNgtZCYx5A2XI9LItwAzbQBqlWhmz6It1btypnpm+JoJXJunh/JSOO6l9AcSjCvu1NT0UfuW1MhLpTS0C2JmX/Slabg6O0GI82E/c8XsYsZmVuXFtMoa/LH9JBardLQbCIITF1y8nd/+b1S1xYSuofQIDAQAB"));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(keySpec);
    }
}
