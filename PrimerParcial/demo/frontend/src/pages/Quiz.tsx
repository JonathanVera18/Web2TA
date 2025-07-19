import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  LinearProgress,
  Chip,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from 'react-query';
import { quizApi, questionApi, attemptApi } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

interface Question {
  id: number;
  text: string;
  options: string[];
  correctAnswer: number;
}

interface Quiz {
  id: number;
  title: string;
  description: string;
  difficulty: string;
  questionCount: number;
  estimatedTime: number;
}

interface QuizAttempt {
  quizId: number;
  userId: number;
  answers: { questionId: number; selectedAnswer: number }[];
  score: number;
  totalQuestions: number;
}

const Quiz: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState<{ [key: number]: number }>({});
  const [timeLeft, setTimeLeft] = useState<number | null>(null);
  const [showResults, setShowResults] = useState(false);
  const [showConfirmSubmit, setShowConfirmSubmit] = useState(false);

  const { data: quiz, isLoading: quizLoading } = useQuery(
    ['quiz', id],
    () => quizApi.getQuiz(id!),
    { enabled: !!id }
  );

  const { data: questions, isLoading: questionsLoading } = useQuery(
    ['questions', id],
    () => questionApi.getQuestions(id!),
    { enabled: !!id }
  );

  const submitAttemptMutation = useMutation(attemptApi.submitQuizAttempt, {
    onSuccess: (data) => {
      toast.success('Quiz submitted successfully!');
      setShowResults(true);
    },
    onError: (error: any) => {
      toast.error(error.message || 'Failed to submit quiz');
    },
  });

  const isLoading = quizLoading || questionsLoading;

  useEffect(() => {
    if (quiz?.estimatedTime) {
      setTimeLeft(quiz.estimatedTime * 60); // Convert to seconds
    }
  }, [quiz]);

  useEffect(() => {
    if (timeLeft !== null && timeLeft > 0) {
      const timer = setTimeout(() => {
        setTimeLeft(timeLeft - 1);
      }, 1000);

      return () => clearTimeout(timer);
    } else if (timeLeft === 0) {
      handleSubmitQuiz();
    }
  }, [timeLeft]);

  const handleAnswerSelect = (questionId: number, answerIndex: number) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [questionId]: answerIndex
    }));
  };

  const handleNextQuestion = () => {
    if (currentQuestionIndex < (questions?.length || 0) - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  const handlePreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleSubmitQuiz = () => {
    if (!user?.id || !quiz || !questions) return;

    const answeredQuestions = Object.keys(selectedAnswers).length;
    const totalQuestions = questions.length;

    if (answeredQuestions < totalQuestions) {
      setShowConfirmSubmit(true);
      return;
    }

    submitQuiz();
  };

  const submitQuiz = () => {
    if (!user?.id || !quiz || !questions) return;

    const answers = Object.entries(selectedAnswers).map(([questionId, selectedAnswer]) => ({
      questionId: parseInt(questionId),
      selectedAnswer
    }));

    const attempt: QuizAttempt = {
      quizId: quiz.id,
      userId: user.id,
      answers,
      score: 0, // Will be calculated on backend
      totalQuestions: questions.length
    };

    submitAttemptMutation.mutate(attempt);
  };

  const formatTime = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const getProgress = () => {
    if (!questions) return 0;
    return ((currentQuestionIndex + 1) / questions.length) * 100;
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!quiz || !questions) {
    return (
      <Box textAlign="center" py={4}>
        <Typography variant="h6" color="error">
          Quiz not found
        </Typography>
      </Box>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const progress = getProgress();

  return (
    <Box>
      {/* Quiz Header */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h4" component="h1">
              {quiz.title}
            </Typography>
            <Chip label={quiz.difficulty} color="primary" />
          </Box>
          <Typography variant="body1" color="text.secondary" mb={2}>
            {quiz.description}
          </Typography>
          
          {/* Progress and Timer */}
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="body2">
              Question {currentQuestionIndex + 1} of {questions.length}
            </Typography>
            {timeLeft !== null && (
              <Typography variant="body2" color={timeLeft < 60 ? 'error' : 'text.secondary'}>
                Time left: {formatTime(timeLeft)}
              </Typography>
            )}
          </Box>
          
          <LinearProgress variant="determinate" value={progress} />
        </CardContent>
      </Card>

      {/* Question Card */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {currentQuestion.text}
          </Typography>

          <FormControl component="fieldset" sx={{ width: '100%' }}>
            <RadioGroup
              value={selectedAnswers[currentQuestion.id] || ''}
              onChange={(e) => handleAnswerSelect(currentQuestion.id, parseInt(e.target.value))}
            >
              {currentQuestion.options.map((option, index) => (
                <FormControlLabel
                  key={index}
                  value={index}
                  control={<Radio />}
                  label={option}
                  sx={{
                    border: '1px solid #e0e0e0',
                    borderRadius: 1,
                    p: 1,
                    mb: 1,
                    '&:hover': {
                      backgroundColor: 'action.hover',
                    },
                  }}
                />
              ))}
            </RadioGroup>
          </FormControl>
        </CardContent>
      </Card>

      {/* Navigation Buttons */}
      <Box display="flex" justifyContent="space-between" mt={3}>
        <Button
          variant="outlined"
          onClick={handlePreviousQuestion}
          disabled={currentQuestionIndex === 0}
        >
          Previous
        </Button>

        <Box>
          {currentQuestionIndex < questions.length - 1 ? (
            <Button
              variant="contained"
              onClick={handleNextQuestion}
              disabled={!selectedAnswers[currentQuestion.id]}
            >
              Next
            </Button>
          ) : (
            <Button
              variant="contained"
              color="success"
              onClick={handleSubmitQuiz}
              disabled={submitAttemptMutation.isLoading}
            >
              {submitAttemptMutation.isLoading ? 'Submitting...' : 'Submit Quiz'}
            </Button>
          )}
        </Box>
      </Box>

      {/* Confirmation Dialog */}
      <Dialog open={showConfirmSubmit} onClose={() => setShowConfirmSubmit(false)}>
        <DialogTitle>Submit Quiz?</DialogTitle>
        <DialogContent>
          <Typography>
            You have answered {Object.keys(selectedAnswers).length} out of {questions.length} questions.
            Are you sure you want to submit the quiz?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowConfirmSubmit(false)}>
            Continue Quiz
          </Button>
          <Button onClick={submitQuiz} variant="contained" color="success">
            Submit Anyway
          </Button>
        </DialogActions>
      </Dialog>

      {/* Results Dialog */}
      <Dialog open={showResults} onClose={() => navigate('/dashboard')} maxWidth="sm" fullWidth>
        <DialogTitle>Quiz Results</DialogTitle>
        <DialogContent>
          <Box textAlign="center" py={2}>
            <Typography variant="h4" color="primary" gutterBottom>
              Quiz Completed!
            </Typography>
            <Typography variant="body1" gutterBottom>
              Your results have been saved. Check your dashboard for detailed statistics.
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => navigate('/dashboard')} variant="contained">
            Go to Dashboard
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Quiz;