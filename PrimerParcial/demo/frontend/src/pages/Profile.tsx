import React, { useState } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  Avatar,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Edit,
  Save,
  Cancel,
  Person,
  Email,
  History,
  TrendingUp,
  EmojiEvents,
} from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { useQuery, useMutation } from 'react-query';
import { useAuth } from '../contexts/AuthContext';
import { userApi, attemptApi } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

interface ProfileFormData {
  username: string;
  email: string;
}

interface QuizAttempt {
  id: number;
  quizTitle: string;
  score: number;
  totalQuestions: number;
  completedAt: string;
}

const Profile: React.FC = () => {
  const { user, updateUser } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);

  const { data: attempts, isLoading: attemptsLoading } = useQuery(
    'userAttempts',
    () => attemptApi.getUserAttempts(user?.id?.toString() || ''),
    {
      enabled: !!user?.id,
      staleTime: 5 * 60 * 1000,
    }
  );

  const updateUserMutation = useMutation(
    (data: ProfileFormData) => userApi.updateUser(user?.id?.toString() || '', data),
    {
      onSuccess: (updatedUser) => {
        updateUser(updatedUser);
        toast.success('Profile updated successfully!');
        setIsEditing(false);
        setShowEditDialog(false);
      },
      onError: (error: any) => {
        toast.error(error.message || 'Failed to update profile');
      },
    }
  );

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<ProfileFormData>({
    defaultValues: {
      username: user?.username || '',
      email: user?.email || '',
    },
  });

  const handleEditClick = () => {
    reset({
      username: user?.username || '',
      email: user?.email || '',
    });
    setShowEditDialog(true);
  };

  const handleSaveProfile = (data: ProfileFormData) => {
    updateUserMutation.mutate(data);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setShowEditDialog(false);
  };

  const getAverageScore = () => {
    if (!attempts || attempts.length === 0) return 0;
    const totalScore = attempts.reduce((acc: number, attempt: QuizAttempt) => acc + attempt.score, 0);
    return (totalScore / attempts.length).toFixed(1);
  };

  const getTotalAttempts = () => {
    return attempts?.length || 0;
  };

  const getHighScores = () => {
    return attempts?.filter((attempt: QuizAttempt) => attempt.score >= 80).length || 0;
  };

  const getRecentAttempts = () => {
    return attempts?.slice(0, 5) || [];
  };

  if (attemptsLoading) {
    return <LoadingSpinner />;
  }

  return (
    <Box>
      {/* Profile Header */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" alignItems="center" mb={3}>
            <Avatar
              sx={{
                width: 80,
                height: 80,
                bgcolor: 'primary.main',
                fontSize: '2rem',
                mr: 3,
              }}
            >
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Box flex={1}>
              <Typography variant="h4" gutterBottom>
                {user?.username}
              </Typography>
              <Typography variant="body1" color="text.secondary" gutterBottom>
                {user?.email}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Member since {new Date().toLocaleDateString()}
              </Typography>
            </Box>
            <Button
              variant="outlined"
              startIcon={<Edit />}
              onClick={handleEditClick}
            >
              Edit Profile
            </Button>
          </Box>
        </CardContent>
      </Card>

      <Grid container spacing={3}>
        {/* Statistics */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Statistics
              </Typography>
              <List>
                <ListItem>
                  <ListItemAvatar>
                    <Avatar sx={{ bgcolor: 'primary.main' }}>
                      <History />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={getTotalAttempts()}
                    secondary="Total Attempts"
                  />
                </ListItem>
                <ListItem>
                  <ListItemAvatar>
                    <Avatar sx={{ bgcolor: 'success.main' }}>
                      <TrendingUp />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={`${getAverageScore()}%`}
                    secondary="Average Score"
                  />
                </ListItem>
                <ListItem>
                  <ListItemAvatar>
                    <Avatar sx={{ bgcolor: 'warning.main' }}>
                      <EmojiEvents />
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={getHighScores()}
                    secondary="High Scores (≥80%)"
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Activity */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Activity
              </Typography>
              {getRecentAttempts().length > 0 ? (
                <List>
                  {getRecentAttempts().map((attempt: QuizAttempt, index: number) => (
                    <React.Fragment key={attempt.id}>
                      <ListItem>
                        <ListItemAvatar>
                          <Avatar
                            sx={{
                              bgcolor: attempt.score >= 80 ? 'success.main' : 'warning.main',
                            }}
                          >
                            {attempt.score >= 80 ? 'A' : 'B'}
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
                        <Chip
                          label={`${attempt.score}%`}
                          color={attempt.score >= 80 ? 'success' : 'warning'}
                          size="small"
                        />
                      </ListItem>
                      {index < getRecentAttempts().length - 1 && (
                        <Divider variant="inset" component="li" />
                      )}
                    </React.Fragment>
                  ))}
                </List>
              ) : (
                <Box textAlign="center" py={4}>
                  <History sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="h6" color="text.secondary" gutterBottom>
                    No activity yet
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

      {/* Edit Profile Dialog */}
      <Dialog open={showEditDialog} onClose={handleCancelEdit} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Profile</DialogTitle>
        <DialogContent>
          <form onSubmit={handleSubmit(handleSaveProfile)}>
            <TextField
              fullWidth
              label="Username"
              variant="outlined"
              margin="normal"
              {...register('username', {
                required: 'Username is required',
                minLength: {
                  value: 3,
                  message: 'Username must be at least 3 characters',
                },
                pattern: {
                  value: /^[a-zA-Z0-9_]+$/,
                  message: 'Username can only contain letters, numbers, and underscores',
                },
              })}
              error={!!errors.username}
              helperText={errors.username?.message}
            />

            <TextField
              fullWidth
              label="Email"
              type="email"
              variant="outlined"
              margin="normal"
              {...register('email', {
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address',
                },
              })}
              error={!!errors.email}
              helperText={errors.email?.message}
            />
          </form>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelEdit} startIcon={<Cancel />}>
            Cancel
          </Button>
          <Button
            onClick={handleSubmit(handleSaveProfile)}
            variant="contained"
            startIcon={<Save />}
            disabled={updateUserMutation.isLoading}
          >
            {updateUserMutation.isLoading ? 'Saving...' : 'Save Changes'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Profile;