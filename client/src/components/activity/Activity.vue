<template>
  <div>
    <div class="profileBanner">
    </div>
    <div class="activityWrap">
      <v-container fluid grid-list-md fill-height fill-width>
        <v-layout row wrap width="600px">
          <v-flex>
            <v-card class="activityContainer" :loading="loadingActivity">
              <h3 id="activityPageTitle" class="activityTitle"> {{ activity_name }} </h3>
              <div id="activityPageDescription" class="activityDescriptionLabel">{{ description }}</div>
              <div id="activityPageVisibility" class="activityLocationLabel">
                {{ "Visibility: " + visibility.replace(/\b\w/g, l => l.toUpperCase()) }}
              </div>
              <div id="activityPageLocation" class="activityLocationLabel">{{ location }}</div>
              <div id="activityPageStartDate" class="activityStartLabel" v-if="continuous === false && loaded === true">
                <h3>
                  Start date: {{ start_date }}</h3></div>
              <div id="activityPageEndDate" class="activityEndLabel" v-if="continuous === false && loaded === true"><h3>
                End
                date: {{ end_date }}</h3></div>
              <div class="activityPageTypeList" id="activityPageTypeListing" v-if="loaded === true">
                Activity Type:
                <span v-for="a in activity_types" :key="a.type_id">
                <span v-if="activity_types.indexOf(a) !== activity_types.length - 1">
                        {{a.name}},
                </span>
                <span v-else>
                        {{a.name}}.
                </span>
            </span>
              </div>
              <div id="activityAuthor" class="activityAuthorLabel" v-if="loaded === true">
                <h3> Created by: {{activity_author_firstname + " " + activity_author_lastname }}</h3>
              </div>
              <div class="activityPageBottomButtons">
                <router-link v-bind:to="'/profile/'+authorId">
                  <button
                      class="genericConfirmButton activityPageBackToProfileButton activityPageBackToProfileButtonSpacing">
                    Back to Profile
                  </button>
                </router-link>
                <router-link v-if="authorId===user.profile_id || user.permission_level > 0"
                             v-bind:to="'/activity_editing/' + activityId">
                  <button
                      class="genericConfirmButton activityPageEditActivityButton activityPageEditActivityButtonSpacing"
                      type="button"
                  >Edit Activity
                  </button>
                </router-link>
                <button v-if="authorId===user.profile_id || user.permission_level > 0"
                        class="genericDeleteButton activityPageDeleteActivityButton activityPageDeleteActivityButtonSpacing"
                        type="button" id="activityPageInfoDeleteButton" v-on:click="deleteActivity()">Delete Activity
                </button>
                <div v-if="!userFollowing">
                  <v-btn v-on:click="followCurrentActivity()" color="#1cca92" outlined rounded large>Follow</v-btn>
                </div>
                <div v-else>
                  <v-btn v-on:click="unFollowCurrentActivity()" color="#f06a6a" outlined rounded large>Un follow</v-btn>
                </div>
              </div>
            </v-card>
          </v-flex>

          <v-flex>
            <v-flex>
              <v-layout row wrap>
                <v-flex>
                  <v-card class="activityPageCard">
                    <h2>Participants / Organisers</h2>
                    <v-tabs
                        v-model="tab"
                        fixed-tabs
                    >
                      <v-tab
                          v-for="item in userTabs"
                          :key="item.tab"
                      >
                        {{ item.tab }}
                      </v-tab>
                    </v-tabs>

                    <v-tabs-items v-model="tab">
                      <v-tab-item
                          v-for="item in userTabs"
                          :key="item.tab"
                      >
                        <v-card flat>
                          <div v-if="item.preview.length === 0">
                            <v-card-text>There are currently no {{ item.tab.toLowerCase() }} for this activity</v-card-text>
                          </div>
                          <div v-else>
                            <v-list-item two-line v-for="user in item.preview" :key="user.email" link @click.stop="">
                              <v-list-item-content>
                                <v-list-item-title v-if="user.middlename != null">
                                  {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                                </v-list-item-title>
                                <v-list-item-title v-else>
                                  {{ user.firstname + " " + user.lastname}}
                                </v-list-item-title>
                                <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                              </v-list-item-content>
                              <v-menu
                                  transition="slide-y-transition"
                                  bottom
                                  right
                                  :close-on-click="false"
                              >
                                <template v-slot:activator="{ on, attrs }">
                                  <v-btn
                                      v-bind="attrs"
                                      v-on="on"
                                      icon
                                  >
                                    <v-icon>mdi-dots-vertical</v-icon>
                                  </v-btn>
                                </template>
                                <v-card>
                                  <v-card-text>Change Role</v-card-text>
                                  <v-switch v-model="roleSwitch"></v-switch>
                                </v-card>
                              </v-menu>
                            </v-list-item>
                          </div>
                        </v-card>
                      </v-tab-item>
                    </v-tabs-items>

                    <v-btn
                        class="activityPageShowMoreButton"
                        height="40px" color="#1cca92"
                        outlined rounded
                        @click.stop="showMoreDialog = true"
                    >Show More
                    </v-btn>
                  </v-card>
                </v-flex>

                <v-dialog
                    v-model="showMoreDialog"
                    max-width="450"
                >
                  <v-tabs
                      v-model="dialogTab"
                      fixed-tabs
                  >
                    <v-tab
                        v-for="item in userTabs"
                        :key="item.tab"
                    >
                      {{ item.tab }}
                    </v-tab>
                  </v-tabs>

                  <v-tabs-items v-model="dialogTab">
                    <v-tab-item
                        v-for="item in userTabs"
                        :key="item.tab"
                    >
                      <div style="overflow-y: scroll; height: 500px"
                      >
                        <v-card flat
                        >
                          <div v-if="item.preview.length === 0">
                            <v-card-text>There are currently no {{ item.tab.toLowerCase() }} for this activity</v-card-text>
                          </div>
                          <div v-else>
                            <v-list-item two-line v-for="user in item.content" :key="user.email" link>
                              <v-list-item-content>
                                <v-list-item-title v-if="user.middlename != null">
                                  {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                                </v-list-item-title>
                                <v-list-item-title v-else>
                                  {{ user.firstname + " " + user.lastname}}
                                </v-list-item-title>
                                <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                              </v-list-item-content>
                              <v-menu
                                      transition="slide-y-transition"
                                      bottom
                                      right
                                      :close-on-click="false"
                              >
                                <template v-slot:activator="{ on, attrs }">
                                  <v-btn
                                          v-bind="attrs"
                                          v-on="on"
                                          icon
                                  >
                                    <v-icon>mdi-dots-vertical</v-icon>
                                  </v-btn>
                                </template>
                                <v-card>
                                  <v-card-text>Change Role</v-card-text>
                                  <v-switch v-model="roleSwitch"></v-switch>
                                </v-card>
                              </v-menu>
                            </v-list-item>
                          </div>
                        </v-card>
                      </div>
                    </v-tab-item>
                  </v-tabs-items>
                  <v-btn
                          height="40px" color="#1cca92"
                          outlined rounded
                  >More Results
                  </v-btn>
                </v-dialog>

                <v-flex>
                  <v-card class="activityPageCard">
                    <h2>Map track</h2>
                    <h3>Coming next sprint!</h3>
                  </v-card>
                </v-flex>

              </v-layout>
            </v-flex>

            <v-flex>
              <v-layout row wrap>
                <v-flex>
                  <v-card class="activityPageCard">
                    <h2>Gallery</h2>
                    <h3>Coming at some stage!</h3>
                  </v-card>
                </v-flex>

                <v-flex>
                  <v-card class="activityPageCard" style="min-height:0;">
                    <h2 style="padding-bottom:10px;">Latest Changes</h2>
                    <v-timeline dense clipped v-for="(update, i) in activityChanges.data" :key="i">
                      <v-timeline-item
                          icon-color="grey lighten-2"
                          small
                      >
                        <v-row justify="space-between">
                          <v-col>
                            <h2 style="font-size:16px;color:grey;font-weight:500;">{{formatDate(update.dateTime)}}</h2>
                            <h2 v-for="(updateText, j) in update.textContext.split('*').slice(1)" :key="j"
                                style="font-size:16px;color:rgba(0,0,0,0.85);">
                              <li>{{updateText}}</li>
                            </h2>
                            <!--                        <h2 style="font-size:16px;color:rgba(0,0,0,0.85);">{{update.textContext}}</h2>-->
                          </v-col>
                        </v-row>
                      </v-timeline-item>
                    </v-timeline>
                  </v-card>
                </v-flex>
              </v-layout>
            </v-flex>

            <v-flex>
              <v-layout row wrap>
                <v-flex>
                  <v-card v-if="visibility === 'restricted'" class="activityPageCard">
                    <h2>Shared Users</h2>
                    <form class="activityPageCardForm">
                      <v-text-field v-model="emailsToAdd" class="activityPageCardTextField mb-5" label="Add email(s)"
                                    outlined rounded clearable hide-details dense></v-text-field>
                      <v-select class="activityPageCardSelect mr-10" v-model="newRole"
                                :items="roleOptions" name="roleValue" required label="Role" outlined hide-details dense
                                rounded></v-select>
                      <v-btn v-on:click="parseEmails()" class="activityPageCardButton" height="40px" color="#1cca92"
                             outlined rounded>Add
                      </v-btn>
                      <h6 class="activityPageErrorMessage" v-if="displayInvalidInputError">{{ invalidInputErrorMessage
                        }}
                      </h6>
                      <h6 class="editSuccessMessage" v-if="displaySharedUsersSuccessMsg">{{ sharedUsersStatusMsg }}
                      </h6>
                      <div class="activityPageCardDiv">
                        <v-card flat>
                          <v-list-item two-line v-for="user in sharedUsers" :key="user[0]">
                            <v-list-item-content>
                              <v-list-item-title v-if="user[2] != null">
                                {{ user[1] + " " + user[2] + " " + user[3]}}
                              </v-list-item-title>
                              <v-list-item-title v-else>
                                {{ user[1] + " " + user[3]}}
                              </v-list-item-title>
                              <v-list-item-subtitle>{{ user.primary_email }}</v-list-item-subtitle>
                            </v-list-item-content>
                          </v-list-item>
                        </v-card>
                      </div>
                    </form>
                  </v-card>
                </v-flex>
              </v-layout>
            </v-flex>
          </v-flex>
        </v-layout>
      </v-container>
    </div>
  </div>
</template>

<script>

  import dateUtil from "@/util/date";
  import {mapActions, mapGetters} from "vuex";
  import {apiActivity, apiUser} from "../../api";

  export default {
    name: "ActivityPageInfo",
    data() {
      return {
        activity_name: "",
        activity_author_firstname: "",
        activity_author_lastname: "",
        continuous: false,
        description: "",
        activity_types: [],
        activityChanges: [],
        visibility: "",
        start_date: null,
        end_date: null,
        location: "",
        loaded: false,
        authorId: null,
        activityId: null,
        loadingActivity: true,
        userFollowing: null,
        tab: null,
        dialogTab: null,
        showMoreDialog: false,
        newRole: "participant",
        emailsToAdd: "",
        roleOptions: [
          {value: "participant", text: "Participant"},
          {value: "organiser", text: "Organiser"},
          {value: "follower", text: "Follower"}
        ],
        displayInvalidInputError: false,
        invalidInputErrorMessage: "",
        participantsPageInfo: {
          defaultPage: 0, currentPage: 0, defaultSize: 10, currentSize: 10,
        },
        organisersPageInfo: {
          defaultPage: 0, currentPage: 0, defaultSize: 10, currentSize: 10,
        },
        userTabs: [
          {tab: 'Participants', content: null, preview: null},
          {tab: 'Organisers', content: null, preview: null},
        ],
        roleSwitch: null,
        sharedUsers: [],
        displaySharedUsersSuccessMsg: false,
        displaySharedUsersErrorMsg: false,
        sharedUsersStatusMsg: ""
      }
    },

    computed: {
      ...mapGetters(['activity']),
      ...mapGetters(['user']),
    },
    mounted: function () {
      if (!this.user.isLogin) {
        this.$router.push('/login');
      }
    },
    created: function () {
      this.loadActivity();
      this.getParticipants();
      this.getOrganisers();
      return this.checkFollowing();
    },
    methods: {
      ...mapActions(['updateUserDurationActivities', 'updateUserContinuousActivities', 'getActivityUpdates', 'getParticipants', 'getOrganisers']),

      /**
       * Parses the list of emails the user entered by splitting them and removing any extra spaces. Checks each one is
       * valid by calling validateEmail, and displays an error message stating which email is invalid if any.
       */ async parseEmails() {
        this.displayInvalidInputError = false;
        const separators = [' ', ';'];
        let emails = this.emailsToAdd.split(new RegExp(separators.join('|'), 'g'));
        let emailsAreCorrect = true;
        for (let i = 0; i < emails.length; i++) {
          if (emails[i] === "") {
            emails.splice(i, 1);
            i--;
          }
        }
        for (let email of emails) {
          if (!this.validateEmail(email)) {
            this.invalidInputErrorMessage = "'" + email + "' is an invalid email address.";
            this.displayInvalidInputError = true;
            this.displaySharedUsersSuccessMsg = false;
            emailsAreCorrect = false;
            break; // So that the first invalid email is displayed
          }
        }
        if (emailsAreCorrect) {
          await apiActivity.setActivityMembers(emails, this.newRole, this.authorId, this.activityId)
            .then(response => {
              this.sharedUsersStatusMsg = response.data;
              this.displaySharedUsersSuccessMsg = true;
            })
            .catch(error => {
              console.log(error);
              this.displaySharedUsersSuccessMsg = false;
              this.invalidInputErrorMessage = "Something went wrong, please check the information provided is correct.";
              this.displayInvalidInputError = true;
            })

        }
      },

      /**
       * Checks if an email address is valid. Adapted from
       * https://www.w3resource.com/javascript/form/email-validation.php
       */
      validateEmail(mail) {
        return /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(mail);
      },

      /**
       * Formats the datetime string to the form Aug 4 2020
       */
      formatDate(datetime) {
        let newDate = new Date(datetime);
        let dateString = newDate.toDateString();
        dateString = dateString.slice(4);
        return dateString;
      },
      /**
       * Retrieves participants for the activity
       */
      async getParticipants() {
        try {
          let response = await apiActivity.getParticipants(this.$route.params.activityId, this.participantsPageInfo.currentPage, this.participantsPageInfo.currentSize);
          this.userTabs[0].content = response.data.content;
          if (this.userTabs[0].preview == null) {
            this.userTabs[0].preview = this.userTabs[0].content.slice(0,3);
          }
        } catch (err) {
          console.error(err)
        }
      },
      /**
       * Retrieves organisers for the activity
       */
      async getOrganisers() {
        try {
          let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.organisersPageInfo.currentPage, this.organisersPageInfo.currentSize);
          this.userTabs[1].content = response.data.content;
          if (this.userTabs[1].preview == null) {
            this.userTabs[1].preview = this.userTabs[1].content.slice(0,3);
          }
        } catch (err) {
          console.error(err)
        }
      },
      /**
       * Deletes the current activity
       */
      async deleteActivity() {
        try {
          let response = await apiActivity.deleteActivity(this.user.profile_id, this.$route.params.activityId)
          response = await apiUser.getUserContinuousActivities(this.user.profile_id)
          this.updateUserContinuousActivities(response.data);
          response = await apiUser.getUserDurationActivities(this.user.profile_id)
          this.updateUserDurationActivities(response.data);
          this.$router.push("/profile/" + this.authorId);
        } catch (err) {
          console.error(err)
        }
      },
      /**
       * Requests the activity and loads its information
       * @returns {Promise<void>}
       */
      async loadActivity() {
        if (this.$route.params.activityId == null || this.$route.params.activityId === "") {
          this.$router.push('/profile');
        } else {
          let tempActivityData = await apiActivity.getActivityById(this.$route.params.activityId);
          this.activityChanges = await apiActivity.getActivityUpdates(this.$route.params.activityId, 0, 5);
          if (tempActivityData === "Invalid permissions") {
            this.$router.push('/profile');
          } else {
            this.activityId = tempActivityData.id;
            this.activity_name = tempActivityData.activity_name;
            this.continuous = tempActivityData.continuous;
            this.description = tempActivityData.description;
            this.activity_types = tempActivityData.activity_type;
            this.visibility = tempActivityData.visibility;
            if (this.visibility === "restricted") {
              apiActivity.getSharedUsers(this.activityId).then(
                (response) => {
                  this.sharedUsers = response.data.content;
                })
            }
            this.start_date = dateUtil.getFormatDate(new Date(tempActivityData.start_time));
            this.end_date = dateUtil.getFormatDate(new Date(tempActivityData.end_time));
            this.location = tempActivityData.location;
            this.activity_author_firstname = tempActivityData.author.firstname;
            this.activity_author_lastname = tempActivityData.author.lastname;
            this.authorId = tempActivityData.author.profile_id;
            this.loaded = true;
            this.loadingActivity = false;
          }
        }
      },
      /**
       * Checks if user is following current activity and sets userFollowing which is used to determine if
       * the follow button should be for following or unfollowing
       * @returns {Promise<void>}
       */
      async checkFollowing() {
        await apiUser.isUserFollowingActivitiy(this.user.profile_id, this.$route.params.activityId)
          .then((response) => {
            this.userFollowing = response.data !== false;
          })
          .catch((error) => {
            if (error.response.status === 404) {
              this.userFollowing = false;
            }
          });
      },
      /**
       * Makes api call to allow a user to follow current activity after follow button is pressed
       * @returns {Promise<void>}
       */
      async followCurrentActivity() {
        await apiActivity.followActivity(this.user.profile_id, this.$route.params.activityId).then(response => {
          if (response.status === 201) {
            this.userFollowing = true;
          }
        });
      },

      /**
       * Makes api call to allow a user to un follow current activity after un follow button is pressed
       * @returns {Promise<void>}
       */
      async unFollowCurrentActivity() {
        await apiActivity.unfollowActivity(this.user.profile_id, this.$route.params.activityId).then(response => {
          if (response.status === 200) {
            this.userFollowing = false;
          }
        });
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/activityStyle.css";
  @import "../../../public/styles/pages/profileStyle.css";
</style>
