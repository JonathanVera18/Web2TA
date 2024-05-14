@Entity
@Table(name = "Quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quiz_id;

    private String difficulty_level;
    private String title;

    // Getters and setters...
    public Long getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Long quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getDifficulty_level() {
        return difficulty_level;
    }

    public void setDifficulty_level(String difficulty_level) {
        this.difficulty_level = difficulty_level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
