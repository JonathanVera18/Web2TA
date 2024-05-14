@RunWith(SpringRunner.class)
@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    public void getAllQuizzesTest() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle("test");
        quiz.setDifficulty_level("easy");

        List<Quiz> quizList = Arrays.asList(quiz);

        Mockito.when(quizService.getAllQuizzes()).thenReturn(quizList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(quiz.getTitle())));
    }

    @Test
    public void getQuizByIdTest() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle("test");
        quiz.setDifficulty_level("easy");

        Mockito.when(quizService.getQuizById(1L)).thenReturn(quiz);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(quiz.getTitle())));
    }

    @Test
    public void createQuizTest() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle("test");
        quiz.setDifficulty_level("easy");

        Mockito.when(quizService.createQuiz(Mockito.any(Quiz.class))).thenReturn(quiz);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(quiz)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(quiz.getTitle())));
    }

    @Test
    public void updateQuizTest() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setTitle("test");
        quiz.setDifficulty_level("easy");

        Mockito.when(quizService.updateQuiz(Mockito.any(Quiz.class))).thenReturn(quiz);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(quiz)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(quiz.getTitle())));
    }

    @Test
    public void deleteQuizTest() throws Exception {
        Mockito.doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/quiz/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}