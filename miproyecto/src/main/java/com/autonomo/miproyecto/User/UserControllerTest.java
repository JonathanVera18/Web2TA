@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // ... (Otras pruebas)

    @Test
    public void getUserByIdTest() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void createUserTest() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");

        Mockito.when(userService.updateUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void deleteUserTest() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}