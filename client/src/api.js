import axios from "axios";

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

var instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000,
  withCredentials: true,
});

function activitiesAddDashes(activities) {
  // Replace all spaces in each activity type with "-" to support the json spec
  let parsedActivityTypes = [];
  for (let i = 0; i < activities.length; i++) {
    parsedActivityTypes.push(activities[i].split(' ').join('-'));
  }
  return parsedActivityTypes;
}

export const apiUser = {
  // Update the user's password
  changePassword: (profile_id, old_password, new_password, repeat_password) =>
      instance.put("/profiles/" + profile_id + "/password", {
        old_password: old_password,
        new_password: new_password,
        repeat_password: repeat_password,
      }),
  // Submit user signup information to the server
  signUp: (
      firstname,
      lastname,
      middlename,
      nickname,
      primary_email,
      password,
      bio,
      date_of_birth,
      gender,
      fitness
  ) =>
      instance.post("/profiles", {
        firstname: firstname,
        lastname: lastname,
        middlename: middlename,
        nickname: nickname,
        primary_email: primary_email,
        password: password,
        bio: bio,
        date_of_birth: date_of_birth,
        gender: gender,
        fitness: fitness,
      }),
  // Submit user login request to the server
  login: (email, password) =>
      instance.post("/login", {
        email: email,
        password: password,
      }),
  // Removes session token from local storage and posts server request to remove the token from the database
  logout: () => instance.post("/logout"),
  // Submit user signup information to the server
  editProfile: (
      profile_id,
      firstname,
      lastname,
      middlename,
      nickname,
      primary_email,
      bio,
      date_of_birth,
      gender,
      fitness,
      additional_email,
      passports,
      permission_level,
      activities,
      location
  ) =>
      instance.put("/profiles/" + profile_id, {
        firstname: firstname,
        lastname: lastname,
        middlename: middlename,
        nickname: nickname,
        primary_email: primary_email,
        bio: bio,
        date_of_birth: date_of_birth,
        gender: gender,
        fitness: fitness,
        additional_email: additional_email,
        passports: passports,
        permission_level: permission_level,
        activities: activities,
        location: location,
      }),
  refreshUserData: (profile_id) => instance.get("/profiles/" + profile_id),
  // Add additional emails
  addEmails: (profile_id, additional_email) =>
      instance.post("/profiles/" + profile_id + "/emails", {
        additional_email: additional_email,
      }),
  // Edit the user's emails
  editEmail: (profile_id, primary_email, additional_email) =>
      instance.put("/profiles/" + profile_id + "/emails", {
        primary_email: primary_email,
        additional_email: additional_email,
      }),
  //Get all emails
  getAllEmails: () => instance.get('/emails'),
  getIdByEmail: (email) => instance.get('/email/id/', {
    params:
        {email: email}
  }),
  getUserSessionToken: (profile_id) => instance.get('/token/' + profile_id),
  getUserByToken: () => instance.get('validateLogin'),
  async getUserById(profile_id) {
    let searchedUser = await apiUser.refreshUserData(profile_id).then(
        (response) => {
          return response.data;
        },
        (error) => {
          if (error) {
            return "Invalid permissions";
          }
        }
    );
    return searchedUser;
  },
  searchUsers: (searchTerm, searchType, activityTypes, method, page, size) => instance.get('/profiles/',
      {
        params: {
          [searchType]: searchTerm,
          method: method,
          activity: activitiesAddDashes(activityTypes).join(" "),
          page: page,
          size: size,
        }
      }),
  searchedUser(searchedTerm, searchFilter) {
    let filter = {};
    filter[searchFilter] = searchedTerm;
    filter['method'] = 'OR';
    filter['page'] = 0;
    filter['size'] = 3;

    let searchedUserTerm = apiUser.searchUsers(filter).then(
        response => {
          return response.data;
        },
        error => {
          if (error) {
            return "Invalid permissions";
          }
        }
    );
    return searchedUserTerm;
  },
  getUserContinuousActivities: (profile_id) => instance.get('/profiles/' + profile_id + '/activities/continuous'),
  getUserDurationActivities: (profile_id) => instance.get('/profiles/' + profile_id + '/activities/duration'),

  /**
   * Request to get all activity types from the server
   */
  getActivityTypes: () => instance.get('/activity-types'),

  /**
   * Request to update activity types
   */
  editUserActivityTypes: (profile_id, activities) =>
    instance.put("/profiles/" + profile_id + "/activity-types", {
      activities: activitiesAddDashes(activities),
    }),

  /***
   * API call to promote an user with permission level 0 to admin.
   * @param profile_id the id of the user being promoted.
   * @returns {Promise<AxiosResponse<any>>}
   */
  promoteToAdmin: (profile_id) =>
    instance.put("/profiles/" + profile_id + "/role", {
      "role": "admin"
    }),

  // Request to delete a user account
  deleteUserAccount: (profile_id) => instance.delete(`profiles/${profile_id}`),

    /**
     * Makes a request to update a user's role for an activity
     * @param profileId the id of the user
     * @param activityId the id of the activity
     * @param email the email of the user whose role requires updating
     * @param activityRole the user's role for the activity ie follower, participant, organiser or creator
     * @returns {Promise<AxiosResponse<any>>}
     */
    editUserRoleForActivity: (profileId, activityId, email, activityRole) =>
        instance.put(`/profiles/${profileId}/activities/${activityId}/subscriber`,
            {
                email: email,
                activityRole: activityRole
            }),
    //
    isUserFollowingActivitiy: (userId, activityId) => instance.get('/profiles/' + userId + '/subscriptions/activities/' + activityId),

  /**
   * API call to retrieve the home feed details for a user
   * @param profileId the id of the user that requires feed retrieval
   * @param page the page number
   * @param size how many posts we want for each page
   * @returns {Promise<AxiosResponse<any>>} returns the feed for the user
   */
  getUserFeed: (profileId, page, size) => instance.get(`/profiles/${profileId}/feed`,
    {params: {
      page: page,
      size: size
      }
    }),
};

export const apiActivity = {
  addActivity: (
    author_id,
    name,
    continuous,
    start_time,
    end_time,
    description,
    location,
    activity_types,
    visibility
  ) =>
    instance.post("/profiles/" + author_id + "/activities", {
      activity_name: name,
      continuous: continuous,
      start_time: start_time,
      end_time: end_time,
      description: description,
      location: location,
      activity_type: activitiesAddDashes(activity_types),
      visibility: visibility
    }),

  editActivity: (
    author_id,
    name,
    continuous,
    start_time,
    end_time,
    description,
    location,
    activity_types,
    visibility,
    activity_id
  ) =>
    instance.put("/profiles/" + author_id + "/activities/" + activity_id, {
      activity_name: name,
      continuous: continuous,
      start_time: start_time,
      end_time: end_time,
      description: description,
      location: location,
      activity_type: activitiesAddDashes(activity_types),
      visibility: visibility,
      activity_id: activity_id,
    }),

  getActivity: (activityId) => instance.get(`/activities/${activityId}`),

  getActivityUpdates: (activityId, page, size) => instance.get(`/activities/${activityId}/changes/`, {
      params: {
          page: page,
          size: size
      }
  }),

  addActivityAchievement: (profileId, activityId, name, description, resultType) =>
      instance.post(`/profiles/${profileId}/activities/${activityId}/achievements`,{
      name: name,
      description: description,
      result_type: resultType
  }),

  editActivityAchievement: (profileId, activityId, achievementId, name, description, resultType) =>
      instance.put(`/profiles/${profileId}/activities/${activityId}/achievements/${achievementId}`, {
      name: name,
      description: description,
      result_type: resultType
  }),


 deleteActivityAchievement: (profileId, activityId, achievementId) =>
    instance.delete(`/profiles/${profileId}/activities/${activityId}/achievements/${achievementId}`),

  getActivityAchievement: (profileId, activityId) =>
    instance.get(`/profiles/${profileId}/activities/${activityId}/achievements`),

  deleteActivity: (authorId, activityId) =>
      instance.delete(`/profiles/${authorId}/activities/${activityId}`),

  getParticipants: (activityId, page, size) =>
    instance.get(`/activities/${activityId}/participants/`, {
      params: {
        page: page,
        size: size
      }
    }),

  getOrganisers: (activityId, page, size) =>
    instance.get(`/activities/${activityId}/organizers/`, {
      params: {
        page: page,
        size: size
      }
    }),

  async getActivityById(activityId) {
    let activity = await apiActivity.getActivity(activityId).then(
        (response) => {
          for (let i = 0; i < response.data.activity_type.length; i++) {
            response.data.activity_type[i].name = response.data.activity_type[i].name.replace(/-/g, " ")
          }
          return response.data;
        },
        (error) => {
          if (error) {
            return "Invalid permissions";
          }
        }
    );
    return await activity;
  },

  /***
   * Makes a request to set the number of people that can view a restricted activity.
   * @param emails: the emails of the users that can view the activity.
   * @param role: the role the users have in that activity.
   * @param profileId: the id of the creator of the activity.
   * @param activityId: the activity.
   * @returns a status code saying if the update was successful or was rejected for some reason.
   */
  async setActivityMembers(emails, role, profileId, activityId) {
    let membersList = [];
    for (let email of emails) {
      let userDetails = {"email": email, "role": role};
      membersList.push(userDetails);
    }
      return await instance.put(`/profiles/${profileId}/activities/${activityId}/visibility`, {
            "visibility": "restricted",
            "accessors": membersList
        }
    );
  },

  followActivity: (profileId, activityId) =>
      instance.post(`/profiles/${profileId}/subscriptions/activities/${activityId}`),

  unfollowActivity: (profileId, activityId) =>
      instance.delete(`/profiles/${profileId}/subscriptions/activities/${activityId}`),
  /**
   * Sends a request to add a result
   * @param profileId Id of profile
   * @param achievementId Id of achievement to add result to
   * @param resultValue value of result
   * @returns {Promise<AxiosResponse<T>>}
   */
  addResult: (profileId, achievementId, resultValue) =>
      instance.post(`/profiles/${profileId}/achievements/${achievementId}/results`, {
        value: resultValue,
      }),
  /**
   * Sends a get request to retrieve one result
   * @param profileId Id of profile
   * @param achievementId Id of achievement
   * @param resultId Id of result
   * @returns {Promise<AxiosResponse<T>>}
   */
  getOneResult: (profileId, achievementId, resultId) =>
      instance.get(`/profiles/${profileId}/achievements/${achievementId}/results/${resultId}`),
  /**
   * Sends a get request to retrieve all results
   * @param achievementId
   * @returns {Promise<AxiosResponse<any>>}
   */
  getResults: (achievementId) =>
    instance.get(`/activities/achievements/${achievementId}/results`),

    getSharedUsers: (activityId, currentPage, size) => instance.get(`/activities/${activityId}/shared`,{
        params: {
            page: currentPage,
            size: size
        }
    }),

    editUserActivityRole: (profileId, activityId, role, email) =>
        instance.put(`/profiles/${profileId}/activities/${activityId}/subscriber`, {
            "subscriber": {
                "email": email,
                "role": role
            }
        }),

    optOutOfActivityRole: (activityId, userEmail) => instance.delete(`/activities/${activityId}/roles/${userEmail}`),

    getActivityStats: (activityId) =>
        instance.get(`/activities/${activityId}/stats`)
};
