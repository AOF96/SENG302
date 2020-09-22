<template>
  <div @click="showLocations = false">
    <v-snackbar
        v-model="snackbar"
    >
      {{ snackbarText }}
      <v-btn
          color="primary"
          text
          @click="snackbar = false"
      >
        Close
      </v-btn>
    </v-snackbar>
    <v-overlay style="z-index: 1000" :value="overlayLoader">
      <v-progress-circular indeterminate size="64"></v-progress-circular>
    </v-overlay>
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <h1 id="createHeading">Edit Activity</h1>
        <h1 id="createSubheading">Make changes to your activity.</h1>
        <form class="CreateActivityFormContainer">
          <v-card style="margin-top:20px;border-radius:15px;" :loading="pageLoading" :disabled="pageLoading">
            <v-tabs v-model="tabs" grow show-arrows>
              <v-tab>
                Basic Info*
              </v-tab>
              <v-tab>
                Time/Date*
              </v-tab>
              <v-tab>
                Location
              </v-tab>
              <v-tab>
                Achievements
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="tabs">
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel" for="name">Activity Name*</label>
                  <input class="editActivityInput" type="text" id="name" v-model="activity_name"
                         placeholder="Activity Name" required/>
                  <label class="editActivityLabel">Visibility</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="visibility" row>
                      <v-radio
                          label="Public"
                          color="green"
                          value="public"
                      ></v-radio>
                      <v-radio
                          label="Restricted"
                          color="orange"
                          value="restricted"
                      ></v-radio>
                      <v-radio
                          label="Private"
                          color="red"
                          value="private"
                      ></v-radio>
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" for="desc">Description</label>
                  <textarea
                      class="editActivityTextarea"
                      maxlength="255"
                      type="text"
                      id="desc"
                      v-model="description"
                      placeholder="Activity Description">
                  </textarea>
                  <label class="editActivityLabel">Activity Types*</label>
                  <v-select style="margin:0 20px;margin-top:5px;"
                            v-model="activity_types_selected"
                            :items="activities_option"
                            attach
                            chips
                            label="Select Activity Types"
                            multiple
                            rounded
                            outlined
                            :loading="activityTypesLoading"
                            :disabled="activityTypesLoading"
                  ></v-select>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel">Continuous?</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="duration" v-on:change="setDuration" row>
                      <v-radio
                          label="Continuous"
                          color="green"
                          value="continuous"
                      ></v-radio>
                      <v-radio
                          label="Duration"
                          color="green"
                          value="duration"
                      ></v-radio>
                    </v-radio-group>
                  </v-row>
                  <div v-if="isDuration">
                    <label class="editActivityLabel" id="startDateLabel" for="start_date">Start Date*</label>
                    <input class="editActivityInput" type="date" id="start_date" v-model="start_date"/>

                    <label class="editActivityLabel" id="endDateLabel" for="end_date">End Date*</label>
                    <input class="editActivityInput" type="date" id="end_date" v-model="end_date"/>

                    <label class="editActivityLabel" id="startTimeLabel" for="start_time">Start Time*</label>
                    <input class="editActivityInput" type="time" id="start_time" v-model="start_time"/>

                    <label class="editActivityLabel" id="endTimeLabel" for="end_time">End Time*</label>
                    <input class="editActivityInput" type="time" id="end_time" v-model="end_time"/>
                  </div>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <ActivityLocationSettings v-on:set-location="setLocationFromComponent"/>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat>
                  <v-card-text style="text-align: center;color:grey;" v-if="achievements.length === 0">No Achievements
                    Yet
                  </v-card-text>
                  <v-row justify="center" align="center" no-gutters v-for="(achievement, index) in achievements"
                         v-bind:key="index">
                    <v-card color="component lighten-1" style="width:100%;padding:20px;margin:15px;border-radius: 15px;">
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.resultType}}</v-card-text>
                      <v-card-title style="padding: 0;word-break: break-word;">{{achievement.name}}</v-card-title>
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.description}}</v-card-text>
                      <v-spacer></v-spacer>
                      <v-menu bottom left>
                        <template v-slot:activator="{ on, attrs }">
                          <v-btn
                              text
                              rounded
                              style="position:absolute;width:36px;min-width:36px;right:15px;top:15px;"
                              v-bind="attrs"
                              v-on="on"
                          >
                            <v-icon>mdi-dots-vertical</v-icon>
                          </v-btn>
                        </template>

                        <v-list>
                          <v-list-item @click="setTempAchievement(achievement)">
                            <v-list-item-title>Edit</v-list-item-title>
                          </v-list-item>
                          <v-list-item @click="openDeletePopUp(achievement)">
                            <v-list-item-title>
                              Remove
                            </v-list-item-title>
                          </v-list-item>
                        </v-list>
                      </v-menu>
                    </v-card>
                  </v-row>
                  <v-dialog v-model="editDialog" width="400px">
                    <v-card class="pa-4" style="border-radius: 15px;">
                      <v-card-title style="font-size: 18px !important;padding: 0 0 10px;">Edit Achievement</v-card-title>
                        <v-row no-gutters style="margin-top: 10px;">
                          <v-col>
                            <v-text-field
                                v-model="tempTitle" label="Achievement Title"
                                placeholder="Achievement title" rounded outlined dense
                                required style="margin-right: 5px" color="white"
                                id="achievementTitle"/>
                          </v-col>
                          <v-col cols="5.5">
                            <v-select
                                id="achieveType"
                                style="margin-left: 5px;"
                                v-model="tempResultType"
                                :items="options"
                                label="Select achievement type"
                                rounded
                                outlined
                                dense
                                color="white"
                            />
                          </v-col>
                        </v-row>
                        <v-row no-gutters>
                          <v-col style="margin-top: 10px;">
                            <v-textarea
                                label="Achievement Description"
                                maxlength="255"
                                type="text"
                                v-model="tempDescription"
                                rows="2"
                                row-height="30"
                                outlined
                                id="achievementDescription"
                                densecolor="white"
                                placeholder="Achievement Description">
                            </v-textarea>
                          </v-col>
                        </v-row>
                        <v-row no-gutters style="margin-top: 15px">
                          <v-spacer></v-spacer>
                          <v-btn color="#f06a6a"
                                 style="margin-left: 10px"
                                 rounded
                                 text
                                 right
                                 :disabled="overlayLoader" @click="editDialog = false">Cancel
                          </v-btn>
                          <v-btn color="#3bb18b"
                                 style="margin-left: 10px"
                                 rounded
                                 text
                                 right
                                 :disabled="overlayLoader"
                                 @click="saveEditedAchievement(tempTitle, tempDescription, tempResultType)">Save
                          </v-btn>
                        </v-row>
                    </v-card>
                  </v-dialog>
                  <v-divider></v-divider>
                  <v-row justify="center" no-gutters v-if="addAchievement" id="addAchievementBox">
                    <v-card style="padding:10px;padding-top:15px;border-radius:15px;width:100%;margin: 15px;"
                            color="colouredComponent">
                      <form>
                        <v-row no-gutters style="margin-bottom:10px;">
                          <v-col>
                            <v-text-field
                                style="margin-right: 5px;"
                                v-model="achieveTitle"
                                label="Achievement Title"
                                rounded
                                required
                                outlined
                                dense
                                color="white"
                                dark
                            ></v-text-field>
                          </v-col>
                          <v-col>
                            <v-select
                                style="margin-left: 5px;"
                                id="achieveType"
                                v-model="achieveType"
                                :items="options"
                                label="Select achievement type"
                                rounded
                                outlined
                                dense
                                color="white"
                                dark
                            />
                          </v-col>
                        </v-row>
                        <v-row no-gutters style="margin-bottom:5px;">
                          <v-col>
                            <v-textarea
                                v-model="achieveDesc"
                                rounded
                                label="Achievement Description"
                                placeholder="Enter Achievement Description"
                                rows="2"
                                row-height="30"
                                outlined
                                dense
                                color="white"
                                dark
                            ></v-textarea>
                          </v-col>
                        </v-row>
                        <v-row no-gutters>
                          <v-spacer></v-spacer>
                          <v-btn
                              v-on:click="cancelAddAchievement()"
                              color="white"
                              rounded
                              text
                              right
                              dark
                              :disabled="overlayLoader"
                          >
                            Cancel
                          </v-btn>
                          <v-btn
                              v-on:click="addNewAchievement(achieveTitle, achieveDesc, achieveType)"
                              color="#3bb18b"
                              style="background-color:white;margin-left: 10px"
                              rounded
                              text
                              right
                              dark
                              :disabled="overlayLoader"
                          >
                            Add
                          </v-btn>
                        </v-row>
                      </form>
                    </v-card>
                  </v-row>
                  <div class="text-center" style="padding-bottom:15px;" v-if="!addAchievement">
                    <v-btn style="margin-top:25px;" class="mx-2" fab dark outlined color="primary"
                           v-on:click="addAchievement = true">
                      <v-icon dark>mdi-plus</v-icon>
                    </v-btn>
                    <v-card-text style="font-weight: 400;color:#1dca92;font-size: 16px">Add Achievement</v-card-text>
                  </div>
                </v-card>
              </v-tab-item>
            </v-tabs-items>

            <v-row justify="center">
              <v-dialog
                  v-model="deleteDialog"
                  max-width="290"
              >
                <v-card>
                  <v-card-title class="headline">Delete achievement</v-card-title>

                  <v-card-text>
                    Are you sure you want to delete this achievement?
                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                        color="green darken-1"
                        text
                        @click="deleteAchievement(tempAchievement)"
                    >
                      Confirm
                    </v-btn>

                    <v-btn
                        color="green darken-1"
                        text
                        @click="deleteDialog = false"
                    >
                      Cancel
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </v-row>


            <v-divider></v-divider>
            <v-row no-gutters>
              <v-btn
                  v-on:click="deleteActivity()"
                  style="margin:15px 20px;"
                  color="red"
                  rounded
                  outlined
                  right
                  :disabled="overlayLoader"
              >
                Delete Activity
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn
                  v-on:click="visibilityOrSave"
                  style="margin:15px 20px;"
                  color="primary"
                  rounded
                  outlined
                  right
                  :disabled="overlayLoader"
              >
                Save Activity
              </v-btn>
            </v-row>
          </v-card>
        </form>
      </div>
      <div>
        <v-dialog v-model="visibilityUpdateDialog" persistent max-width="400">
          <v-card style="border-radius: 15px;padding:10px 0;">
            <v-card-title class="headline">Update Activity Visibility</v-card-title>
            <v-card-text>{{ visibilityUpdateMessage }}</v-card-text>
            <v-card-text>There are currently {{groups[0].amount}} {{groups[0].name}}, {{groups[1].amount}}
              {{groups[1].name}} and {{groups[2].amount}} {{groups[2].name}}.
            </v-card-text>
            <div v-if="(tempVisibility === 'public' && visibility === 'restricted')">
              <v-list-item v-for="group in groups" :key="group.name">
                <v-checkbox
                    v-model="group.active"
                    v-bind:label="'Keep ' + group.name"
                    color="success"
                    hide-details
                ></v-checkbox>
              </v-list-item>
            </div>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="green darken-1" rounded text @click="visibilityUpdateDialog = false">Cancel</v-btn>
              <v-btn color="green darken-1" rounded text v-on:click="updateVisibilityAndGroups"
                     @click="visibilityUpdateDialog = false">Confirm
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from "vuex";
  import {apiUser, apiActivity} from "../api";
  import router from "../router";
  import ActivityLocationSettings from "./activity/settings/ActivityLocationSettings";

  export default {
    components: {
      ActivityLocationSettings
    },
    data() {
      return {
        tabs: null,
        selected_activity: "Activity Type",
        activities_option: [],
        location: null,
        duration: "duration",
        activity_types_selected: [],
        start_date: null,
        end_date: null,
        start_time: null,
        end_time: null,
        activity_name: "",
        combinedEndTime: null,
        combinedStartTime: null,
        continuous: false,
        description: "",
        author_id: null,
        visibility: null,
        isLoading: false,
        snackbar: false,
        snackbarText: "",
        overlayLoader: false,
        pageLoading: true,
        activityTypesLoading: true,
        activityAchievements: [],
        addAchievement: false,
        options: ["Word", "Quantity", "Time", "Money"],
        achieveTitle: "",
        achieveDesc: "",
        achieveType: "",
        achievements: [],
        editDialog: false,
        tempAchievement: null,
        tempTitle: null,
        tempDescription: null,
        tempResultType: null,
        tempVisibility: "null",
        deleteDialog: false,
        confirmDelete: false,
        visibilityUpdateDialog: false,
        visibilityUpdateMessage: "",
        groups: [
          {name: "followers", active: true, amount: 0},
          {name: "participants", active: true, amount: 0},
          {name: "organisers", active: true, amount: 0},
        ],
      };
    },

    computed: {
      ...mapGetters(["user"]),
      isDuration() {
        return this.duration == "duration";
      },
    },
    created: async function () {
      console.log("pass")
      await this.loadActivity();
      // Ensures only activity types from the database can be selected and cannot select ones already selected
      await apiUser
          .getActivityTypes()
          .then(response => {
            this.activities_option = response.data;
            for (let i = 0; i < this.activities_option.length; i++) {
              this.activities_option[i] = this.activities_option[i].replace(/-/g, " ")
            }
            this.activityTypesLoading = false;
          })
          .catch(error => console.log(error));
    },
    methods: {
      ...mapActions(["createActivity", "updateUserContinuousActivities", "updateUserDurationActivities",
        "addActivityAchievement", "editActivityAchievement", "deleteActivityAchievement", "getDataFromUrl",
        "getActivityAchievement"]),

      /**
       * Sets location from edit location component
       */
      setLocationFromComponent(newLocation) {
        this.location = newLocation;
      },

      /**
       * Resets achievement input
       */
      cancelAddAchievement() {
        this.addAchievement = false;
        this.achieveTitle = "";
        this.achieveDesc = "";
        this.achieveType = "";
      },
      /**
       * The function adds the achievement to the list of achievements.
       * */

      async addNewAchievement(title, description, type) {
        if (title === null || title.trim() === "") {
          this.displayError("Please enter a title.");
        } else if (this.type === null || type.trim() === "") {
          this.displayError("Please enter an achievement type.");
        } else {
          await apiActivity.addActivityAchievement(this.user.profile_id, this.$route.params.activityId, title, description, type.toUpperCase());
          let tempAchievements = await apiActivity.getActivityAchievement(this.author_id, this.$route.params.activityId);
          this.achievements = tempAchievements.data;
          for (let i = 0; i < this.achievements.length; i++) {
            this.achievements[i].resultType = this.achievements[i].resultType.toLowerCase();
            this.achievements[i].resultType = this.achievements[i].resultType[0].toUpperCase() + this.achievements[i].resultType.slice(1);
          }
          this.cancelAddAchievement();
        }
      },

      /**
       * The function over writes the saved achievement if the user decides to edit it before saving the activity.
       * */
      saveEditedAchievement(title, description, type) {
        this.editDialog = false;
        if (title === null || title.trim() === "") {
          this.displayError("Please enter a title.");
        } else {
          this.achievements[this.index] = {'name': title, 'description': description, 'resultType': type};
          apiActivity.editActivityAchievement(this.user.profile_id, this.$route.params.activityId, this.tempAchievement.id, title, description, type.toUpperCase());
          apiActivity.getActivityAchievement(this.user.profile_id, this.$route.params.activityId);
          for (let i = 0; i < this.achievements.length; i++) {
            this.achievements[i].resultType = this.achievements[i].resultType.toLowerCase();
            this.achievements[i].resultType = this.achievements[i].resultType[0].toUpperCase() + this.achievements[i].resultType.slice(1);
          }
          this.loadActivity()
          this.tempAchievement = null;
          this.index = null;
          this.tempTitle = null;
          this.tempDescription = null;
          this.tempResultType = null;
        }

      },

      /**
       * Used to manage the delete pop up box when deleting an achievement.
       * Makes an api call and checks if any results are associated with the selected achievement.
       * */
      async openDeletePopUp(achievement) {
        let result = await apiActivity.getResults(achievement.id)
        if (result.data.length > 0) {
          this.displayError("This achievement has results associated with it. You cannot delete it anymore.");
          return;
        }
        this.deleteDialog = true;
        this.tempAchievement = achievement;
      },
      /**
       * The function deletes a specific achievements from the list of achievement.
       * */
      deleteAchievement(achievement) {
        apiActivity.deleteActivityAchievement(this.user.profile_id, this.$route.params.activityId, achievement.id);
        this.loadActivity();
        this.tempAchievement = null;
        this.deleteDialog = false;
      },

      /**
       * Assigns the temp achievement to the selected achievement form the list of achievements.
       * Makes an api call and checks if any results are associated with the selected achievement.
       **/
      async setTempAchievement(achievement) {

        let result = await apiActivity.getResults(achievement.id)
        if (result.data.length > 0) {
          this.displayError("This achievement has results associated with it. You cannot edit it anymore.");
          return;
        }
        this.index = this.achievements.indexOf(achievement);
        this.tempAchievement = this.achievements[this.index];
        this.tempTitle = achievement.name;
        this.tempDescription = achievement.description;
        this.tempResultType = achievement.resultType.toLowerCase();
        this.tempResultType = this.tempResultType[0].toUpperCase() + this.tempResultType.slice(1);
        this.editDialog = true
      },

      /**
       * Uses activity id from url to request activity data.
       */
      async loadActivity() {
        if (
            this.$route.params.activityId == null ||
            this.$route.params.activityId == ""
        ) {
          this.$router.push("/profile");
        } else {
          var tempActivityData = await apiActivity.getActivityById(
              this.$route.params.activityId
          );
          if (tempActivityData == "Invalid permissions") {
            this.$router.push("/profile");
          } else {
            let tempAchievements = await apiActivity.getActivityAchievement(tempActivityData.author.profile_id, this.$route.params.activityId);
            this.achievements = tempAchievements.data;
            for (let i = 0; i < this.achievements.length; i++) {
              this.achievements[i].resultType = this.achievements[i].resultType.toLowerCase();
              this.achievements[i].resultType = this.achievements[i].resultType[0].toUpperCase() + this.achievements[i].resultType.slice(1);
            }
            this.pageLoading = false;
            this.activity_name = tempActivityData.activity_name;
            this.continuous = tempActivityData.continuous;
            if (this.continuous) {
              this.duration = "continuous";
            } else {
              this.duration = "duration";
              this.end_time = this.formatTime(tempActivityData.end_time);
              this.start_time = this.formatTime(tempActivityData.start_time);
              this.start_date = this.formatDate(tempActivityData.start_time);
              this.end_date = this.formatDate(tempActivityData.end_time);
            }
            this.author_id = tempActivityData.author.profile_id;
            this.description = tempActivityData.description;
            this.activity_type = tempActivityData.activity_type.slice();
            this.location = tempActivityData.location;
            this.visibility = tempActivityData.visibility;
            this.tempVisibility = tempActivityData.visibility;

            this.getActivityStats();

            for (let i = 0; i < tempActivityData.activity_type.length; i++) {
              tempActivityData.activity_type[i].name = tempActivityData.activity_type[i].name.replace(/-/g, " ")
            }
            this.activity_types_selected = tempActivityData.activity_type.map(
                e => e.name
            );
          }
        }
      },

      /**
       * Shows/hides date and time selection if duration is duration/continuous
       */
      setDuration() {
        this.start_date = null;
        this.end_date = null;
        this.start_time = null;
        this.end_time = null;
      },
      /**
       * This function converts the milli seconds to the format YYYY-MM-DD
       */
      formatDate(date) {
        let d = new Date(date);
        let month = "" + (d.getMonth() + 1);
        let day = "" + d.getDate();
        let year = d.getFullYear();
        if (month.length < 2) month = "0" + month;
        if (day.length < 2) day = "0" + day;
        return [year, month, day].join("-");
      },

      formatTime(date) {
        let d = new Date(date);
        let hour = "" + d.getHours();
        let minutes = "" + d.getMinutes();
        if (hour.length < 2) hour = "0" + hour;
        if (minutes.length < 2) minutes = "0" + minutes;
        return [hour, minutes].join(":");
      },

      /**
       * Adds activity type to selected options
       */
      selectActivityType() {
        if (this.selected_activity !== undefined) {
          this.activity_types_selected.push(this.selected_activity);
          let index = this.activities_option.indexOf(this.selected_activity);
          if (index !== -1) {
            this.activities_option.splice(index, 1);
          }
        }
      },
      /**
       * Removes activity type from selection
       */
      removeActivityType(addedActivity) {
        this.activities_option.push(addedActivity);
        let index = this.activity_types_selected.indexOf(addedActivity);
        if (index !== -1) {
          this.activity_types_selected.splice(index, 1);
        }
      },

      /**
       * Combines the times and dates given in the form to a single datetime format
       * Sets datetime to null if continuous activity
       */
      combineDateTime() {
        let currentDate = new Date(Date.now());
        let timeZone = currentDate
            .toString()
            .slice(currentDate.toString().indexOf("+"), 5);

        if (this.duration !== "duration") {
          this.combinedStartTime = null;
          this.combinedEndTime = null;
        } else if (this.start_time === null && this.end_time === null) {
          this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else if (this.start_time === null) {
          this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          this.combinedEndTime =
              this.end_date + "T" + this.end_time + ":00" + timeZone;
        } else if (this.end_time === null) {
          this.combinedStartTime =
              this.start_date + "T" + this.start_time + ":00" + timeZone;
          this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else {
          this.combinedStartTime =
              this.start_date + "T" + this.start_time + ":00" + timeZone;
          this.combinedEndTime =
              this.end_date + "T" + this.end_time + ":00" + timeZone;
        }
      },

      /**
       * Checks if the datetime passes conditions. Ensures time is in the future and start is not later than end
       * @return boolean true if passes, false if fails
       */
      checkTimeContinuity() {
        let currentDate = new Date(Date.now());
        let timeZone = currentDate
            .toString()
            .slice(currentDate.toString().indexOf("+"), 5);

        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        currentDate.setMilliseconds(0);
        if (new Date(this.combinedStartTime) > new Date(this.combinedEndTime)) {
          this.displayError("End time must be after start time.");
          return false;
        } else if (
            currentDate > new Date(this.start_date + "T00:00:00" + timeZone)
        ) {
          this.displayError("Start date must be in the future.");
          return false;
        } else if (
            currentDate.getFullYear() + 2 <
            new Date(this.start_date + "T00:00:00" + timeZone).getFullYear() ||
            currentDate.getFullYear() + 2 <
            new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()
        ) {
          this.displayError("Must be less than 2 years in the future.");
          return false;
        }
        return true;
      },

      /**
       * Check all activity form conditions
       * @return boolean true if passes, false if fails
       */
      checkFormConditions() {
        if (this.activity_name === null || this.activity_name.trim() === "") {
          // Name is empty
          this.displayError("Please select an activity name.");
          this.tabs = 0;
          return false;
        } else if (this.activity_types_selected.length < 1) {
          // No activity types selected
          this.displayError("Please select at least one activity type.");
          this.tabs = 0;
          return false;
        } else if (this.duration !== "duration" && this.duration !== "continuous") {
          // Duration is not set
          this.displayError("Please select a duration.");
          this.tabs = 1;
          return false;
        } else if (this.duration === "duration" &&
            (this.start_date === null || this.end_date === null || this.start_date === "" || this.end_date === "")) {
          // Start or end date not set
          this.displayError("Please select start and end date.");
          this.tabs = 1;
          return false;
        } else if (this.duration === "duration" && !this.checkTimeContinuity()) {
          // Time check failed
          this.tabs = 1;
          return false;
        } else {
          // All passed
          return true;
        }
      },

      /**
       * Determines whether the visibility pop up is displayed or save the edited activity. The displayed message will
       * depend on the visibility change.
       *
       * If the activity visibility changes from (PUBLIC to RESTRICTED), then ask the user what groups they want to keep.
       *
       * If the activity visibility changes from (PUBLIC or RESTRICTED to PRIVATE), then let the user know that they will
       * lose their followers, participants and organisers if they make the activity PRIVATE.
       *
       * If the activity visibility changes from (PRIVATE to PUBLIC) or (RESTRICTED to PUBLIC), then let the user know
       * that all users will be able to see the activity
       *
       * If the activity visibility changes from (PRIVATE to RESTRICTED), then let the user know that only those who they
       * have shared the activity with can see the activity.
       *
       */
      visibilityOrSave() {
        // Checks if the user wants to the visibility of the activity
        if (this.visibility !== this.tempVisibility) {
          this.visibilityUpdateDialog = true;
          this.visibilityUpdateMessage = `You are updating the visibility of this activity from ${this.tempVisibility} to ${this.visibility}.`
          if (this.tempVisibility === "public" && this.visibility === "restricted") {
            // (PUBLIC to RESTRICTED)
            this.visibilityUpdateMessage += " Please select what groups you would like to keep or remove.";
          } else if ((this.tempVisibility === "public" || this.tempVisibility === "restricted") && this.visibility === "private") {
            // (PUBLIC or RESTRICTED to PRIVATE)
            this.visibilityUpdateMessage += " Making the activity private will remove all followers, participants and organisers. Are you sure you want to update the visibility?";
          } else if ((this.tempVisibility === "private" && this.visibility === "public") || (this.tempVisibility === "restricted" && this.visibility === "public")) {
            // (PRIVATE to PUBLIC) or (RESTRICTED to PUBLIC)
            this.visibilityUpdateMessage += " Making the activity public means that all users can view the activity. Are you sure you want to update the visibility?";
          } else if (this.tempVisibility === "private" && this.visibility === "restricted") {
            // (PRIVATE to RESTRICTED)
            this.visibilityUpdateMessage += " Making the activity public means that only users that you have shared this activity with can view it. Are you sure you want to update the visibility?";
          }
        } else {
          this.saveEditedActivity();
        }
      },

      /**
       * Update which groups to keep when activity visibility is changed
       */
      updateVisibilityAndGroups() {
        this.overlayLoader = true;
        if (this.tempVisibility === 'public' && this.visibility === 'restricted') {
          apiActivity.updateVisibilityAndGroups(this.user.profile_id, this.$route.params.activityId, this.visibility,
              this.groups[0].active, this.groups[1].active, this.groups[2].active).then(() => {
            this.saveEditedActivity();
          }).catch((err) => {
            this.displayError(err);
          });
        } else if (this.visibility === 'private') {
          apiActivity.updateVisibilityAndGroups(this.user.profile_id, this.$route.params.activityId, this.visibility,
              false, false, false).then(() => {
            this.saveEditedActivity();
          }).catch((err) => {
            this.displayError(err);
          });
        } else {
          this.saveEditedActivity();
        }
      },

      /**
       * Gets the stats of the activity
       */
      getActivityStats() {
        apiActivity.getActivityStats(this.$route.params.activityId).then((response) => {
          this.groups[0].amount = response.data.followers;
          this.groups[1].amount = response.data.participants;
          this.groups[2].amount = response.data.organisers;
        }).catch((err) => {
          this.displayError(err);
        });
      },

      /**
       * Checks form conditions and sends create activity request if conditions pass
       */
      saveEditedActivity() {
        // Combines dates and times, must be done before checking form
        this.combineDateTime();

        // Checks all activity attribute conditions
        if (!this.checkFormConditions()) {
          return;
        }
        this.overlayLoader = true;

        // Sets duration to a boolean for the request
        this.duration = this.duration !== "duration";

        apiActivity
            .editActivity(
                this.user.profile_id,
                this.activity_name,
                this.duration,
                this.combinedStartTime,
                this.combinedEndTime,
                this.description,
                this.location,
                this.activity_types_selected,
                this.visibility,
                this.$route.params.activityId
            )
            .then(
                response => {
                  if (response) {
                    apiUser
                        .getUserContinuousActivities(this.user.profile_id)
                        .then(response => {
                          this.updateUserContinuousActivities(response.data);
                        });
                    apiUser
                        .getUserDurationActivities(this.user.profile_id)
                        .then(response => {
                          this.updateUserDurationActivities(response.data);
                        });
                    router.push("/activity/" + this.$route.params.activityId);
                  }
                },
                error => {
                  this.overlayLoader = true;
                  if (error) {
                    this.displayError("An error occurred.");
                  }
                }
            );
      },

      deleteActivity() {
        this.overlayLoader = true;
        apiActivity
            .deleteActivity(this.user.profile_id, this.$route.params.activityId)
            .then(response => {
              console.log(response);
              router.push("/profile/" + this.author_id);
            });
      },
      /**
       * Shows error text for given error string
       * @param error
       */
      displayError(error) {
        this.snackbar = true;
        this.snackbarText = error;
      }
    },
  };
</script>
