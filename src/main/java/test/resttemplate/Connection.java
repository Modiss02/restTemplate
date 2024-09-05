package test.resttemplate;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import test.resttemplate.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Connection {
    private final String URL = "http://94.198.50.185:7081/api/users";
    private final RestTemplate restTemplate;
    private List<String> cookies;

    private String part1;
    private String part2;
    private String part3;

    @Autowired
    public Connection(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void app() {

        ResponseEntity<List<User>> users = getAllUser();
        System.out.println(users.getBody());

        User addUser = new User(3L, "James", "Brown", (byte) 35);
        part1 = addUser(addUser).getBody(); // Сохраняем код ответа
        System.out.println(part1);

        addUser.setName("Thomas");
        addUser.setLastName("Shelby");
        part2 = updateUser(addUser).getBody();
        System.out.println(part2);

        part3 = deleteUser(3L).getBody();
        System.out.println(part3);

        String finalCode = part1 + part2 + part3;
        System.out.println(finalCode);
    }

    public ResponseEntity<List<User>> getAllUser() {
        ResponseEntity<List<User>> responseEntity =
                restTemplate.exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<User>>() {});

        cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .peek(System.out::println)
                .collect(Collectors.toList());

        return responseEntity;
    }

    public ResponseEntity<String> addUser(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.COOKIE, String.join(";", cookies));
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(URL, httpEntity, String.class);
        return responseEntity;
    }

    public ResponseEntity<String> updateUser(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.COOKIE, String.join(";", cookies));
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(URL, HttpMethod.PUT, httpEntity, String.class);
        return responseEntity;
    }

    public ResponseEntity<String> deleteUser(Long id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.COOKIE, String.join(";", cookies));
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(URL + "/" + id
                        , HttpMethod.DELETE, httpEntity, String.class);
        return responseEntity;
    }
}