import React from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Chip,
  Avatar,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Divider,
  Paper,
} from '@mui/material';
import {
  Quiz,
  TrendingUp,
  History,
  Star,
  PlayArrow,
  School,
  EmojiEvents,
} from '@mui/icons-material';
import { useQuery } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { quizApi, attemptApi } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

interface Quiz {
  id: number;
  title: string;
  description: string;
  difficulty: string;
  questionCount: number;
  estimatedTime: number;
}

interface QuizAttempt {
  id: number;
  quizTitle: string;
  score: number;
  totalQuestions: number;
  completedAt: string;
}

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const { data: quizzes, isLoading: quizzesLoading } = useQuery(
    'quizzes',
    quizApi.getQuizzes,
    {
      staleTime: 5 * 60 * 1000, // 5 minutes
    }
  );

  const { data: attempts, isLoading: attemptsLoading } = useQuery(
    'userAttempts',
    () => attemptApi.getUserAttempts(user?.id?.toString() || ''),
    {
      enabled: !!user?.id,
      staleTime: 5 * 60 * 1000,
    }
  );

  const isLoading = quizzesLoading || attemptsLoading;

  if (isLoading) {
    return <LoadingSpinner />;
  }

  const recentQuizzes = quizzes?.slice(0, 3) || [];
  const recentAttempts = attempts?.slice(0, 5) || [];
  const totalQuizzes = quizzes?.length || 0;
  const totalAttempts = attempts?.length || 0;
  const averageScore = attempts?.length > 0 
    ? attempts.reduce((acc: number, attempt: QuizAttempt) => acc + attempt.score, 0) / attempts.length 
    : 0;

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty?.toLowerCase()) {
      case 'easy':
        return 'success';
      case 'medium':
        return 'warning';
      case 'hard':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <Box>
      {/* Welcome Section */}
      <Box mb={4}>
        <Typography variant="h4" component="h1" gutterBottom>
          Welcome back, {user?.username}!
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Ready to test your knowledge? Choose a quiz to get started.
        </Typography>
      </Box>

      {/* Statistics Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: 'primary.main', mr: 2 }}>
                  <Quiz />
                </Avatar>
                <Box>
                  <Typography variant="h6">{totalQuizzes}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Available Quizzes
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: 'secondary.main', mr: 2 }}>
                  <History />
                </Avatar>
                <Box>
                  <Typography variant="h6">{totalAttempts}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Quiz Attempts
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: 'success.main', mr: 2 }}>
                  <TrendingUp />
                </Avatar>
                <Box>
                  <Typography variant="h6">{averageScore.toFixed(1)}%</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Average Score
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: 'warning.main', mr: 2 }}>
                  <EmojiEvents />
                </Avatar>
                <Box>
                  <Typography variant="h6">
                    {attempts?.filter((a: QuizAttempt) => a.score >= 80).length || 0}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    High Scores
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Available Quizzes */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6">Available Quizzes</Typography>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/quizzes')}
                  startIcon={<PlayArrow />}
                >
                  View All
                </Button>
              </Box>

              {recentQuizzes.length > 0 ? (
                <Grid container spacing={2}>
                  {recentQuizzes.map((quiz: Quiz) => (
                    <Grid item xs={12} sm={6} key={quiz.id}>
                      <Paper
                        sx={{
                          p: 2,
                          cursor: 'pointer',
                          '&:hover': {
                            boxShadow: 4,
                            transform: 'translateY(-2px)',
                          },
                          transition: 'all 0.2s ease-in-out',
                        }}
                        onClick={() => navigate(`/quiz/${quiz.id}`)}
                      >
                        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                          <Typography variant="h6" noWrap>
                            {quiz.title}
                          </Typography>
                          <Chip
                            label={quiz.difficulty}
                            color={getDifficultyColor(quiz.difficulty) as any}
                            size="small"
                          />
                        </Box>
                        <Typography variant="body2" color="text.secondary" mb={2}>
                          {quiz.description}
                        </Typography>
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                          <Typography variant="caption" color="text.secondary">
                            {quiz.questionCount} questions
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            ~{quiz.estimatedTime} min
                          </Typography>
                        </Box>
                      </Paper>
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Box textAlign="center" py={4}>
                  <School sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="h6" color="text.secondary" gutterBottom>
                    No quizzes available
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Check back later for new quizzes!
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Activity */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Activity
              </Typography>

              {recentAttempts.length > 0 ? (
                <List>
                  {recentAttempts.map((attempt: QuizAttempt, index: number) => (
                    <React.Fragment key={attempt.id}>
                      <ListItem alignItems="flex-start">
                        <ListItemAvatar>
                          <Avatar sx={{ bgcolor: attempt.score >= 80 ? 'success.main' : 'warning.main' }}>
                            <Star />
                          </Avatar>
                        </ListItemAvatar>
                        <ListItemText
                          primary={attempt.quizTitle}
                          secondary={
                            <React.Fragment>
                              <Typography component="span" variant="body2" color="text.primary">
                                {attempt.score}% ({attempt.score}/{attempt.totalQuestions})
                              </Typography>
                              <Typography variant="caption" display="block" color="text.secondary">
                                {new Date(attempt.completedAt).toLocaleDateString()}
                              </Typography>
                            </React.Fragment>
                          }
                        />
                      </ListItem>
                      {index < recentAttempts.length - 1 && <Divider variant="inset" component="li" />}
                    </React.Fragment>
                  ))}
                </List>
              ) : (
                <Box textAlign="center" py={4}>
                  <History sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="h6" color="text.secondary" gutterBottom>
                    No recent activity
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Complete your first quiz to see your activity here!
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;