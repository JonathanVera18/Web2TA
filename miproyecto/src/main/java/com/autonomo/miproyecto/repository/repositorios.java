@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra un usuario por su nombre de usuario
    User findByUsername(String username);

    // Encuentra usuarios por su edad
    List<User> findByAge(int age);

    // Encuentra usuarios por su ciudad
    List<User> findByCity(String city);

    // Guarda o actualiza un usuario
    User save(User user);

    // Elimina un usuario por su nombre de usuario
    void deleteByUsername(String username);

    // Encuentra todos los usuarios ordenados por nombre
    List<User> findAllByOrderByName();
}


@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra quizzes por su nivel de dificultad
    List<Quiz> findByDifficultyLevel(String difficultyLevel);

    // Encuentra quizzes por su título
    Quiz findByTitle(String title);

    // Guarda o actualiza un quiz
    Quiz save(Quiz quiz);

    // Elimina un quiz por su título
    void deleteByTitle(String title);

    // Encuentra todos los quizzes ordenados por título
    List<Quiz> findAllByOrderByTitle();
}
