<template>
  <div>
    <div class="profileBanner">
    </div>
    <div class="activityWrap">
      <v-container fluid grid-list-md fill-height fill-width>
        <v-layout row wrap width="600px">
          <v-snackbar outlined color="error" :timeout="timeout" :value="snackbar" top>{{ errorMessage }}</v-snackbar>
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
              <div id="numberOfFollowers" class="activityAuthorLabel" v-if="loaded === true">
                <h3> Followers: {{numFollowers}}</h3>
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
                <div v-if="userOpttedIn">
                  <v-btn
                          id="optoutButton"
                          height="45px" color="#f06a6a"
                          outlined rounded style="margin-right: 20px"
                          v-on:click="userOpttedIn = false"
                  >Opt-out
                  </v-btn>
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
                            v-model="previewTabs"
                            fixed-tabs
                            id="previewParticipantsOrganisersTabs"
                    >
                      <v-tab
                              v-for="item in userTabs"
                              :key="item.tab"
                      >
                        {{ item.tab }}
                      </v-tab>
                    </v-tabs>
                    <v-tabs-items v-model="previewTabs" id="activityParticipantsOrganisersTabItems">
                      <v-tab-item
                              v-for="item in userTabs"
                              :key="item.tab"
                      >
                        <v-card flat id="participantOrganiserList">
                          <div v-if="item.preview.length === 0">
                            <v-card-text>There are currently no {{ item.tab.toLowerCase() }} for this activity
                            </v-card-text>
                          </div>
                          <div v-else>
                            <v-list-item two-line v-for="profile in item.preview" :key="profile.email" link
                                         @click.stop="">
                              <v-list-item-content>
                                <v-list-item-title v-if="profile.middlename != null">
                                  {{ profile.firstname + " " + profile.middlename + " " +
                                  profile.lastname}}
                                </v-list-item-title>
                                <v-list-item-title v-else>
                                  {{ profile.firstname + " " + profile.lastname}}
                                </v-list-item-title>
                                <v-list-item-subtitle>{{ profile.email }}
                                </v-list-item-subtitle>
                              </v-list-item-content>
                              <div v-if="user.profile_id === authorId || user.permission_level > 0">
                                <v-menu
                                        transition="slide-y-transition"
                                        bottom
                                        right
                                        :close-on-click="true">
                                  <template v-slot:activator="{ on, attrs }">
                                    <v-btn
                                            v-bind="attrs"
                                            v-on="on"
                                            icon>
                                      <v-icon>mdi-dots-vertical</v-icon>
                                    </v-btn>
                                  </template>
                                  <v-card>
                                    <v-card>
                                      <v-list-item
                                              v-on:click="editUserActivityRole(item.tab.toLowerCase(), profile.email)">
                                        <div v-if="item.tab === 'Participants'">
                                          <v-list-item-title>Move to Organiser
                                          </v-list-item-title>
                                        </div>
                                        <div v-else>
                                          <v-list-item-title>Move to
                                            Participants
                                          </v-list-item-title>
                                        </div>
                                      </v-list-item>
                                    </v-card>
                                  </v-card>
                                </v-menu>
                              </div>
                            </v-list-item>
                          </div>
                        </v-card>
                      </v-tab-item>
                    </v-tabs-items>
                    <v-btn
                            id="activityPageShowMoreButton"
                            height="45px" color="#1cca92"
                            outlined rounded
                            @click.stop="showMoreDialog = true"
                    >Show More
                      </v-btn>
                  </v-card>
                </v-flex>

                <v-dialog
                        v-model="showMoreDialog"
                        max-width="450"
                        id="activityPageMoreParticipantsOrganisersDialog"
                >
                  <v-card :loading="loadingParticipantsOrganisersDialog">
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
                              <v-card-text>There are currently no {{
                                item.tab.toLowerCase() }} for this activity
                              </v-card-text>
                            </div>
                            <div v-else>
                              <v-list-item two-line v-for="profile in item.content"
                                           :key="profile.email" link>
                                <v-list-item-content>
                                  <v-list-item-title v-if="profile.middlename != null">
                                    {{ profile.firstname + " " + profile.middlename + " "
                                    + profile.lastname}}
                                  </v-list-item-title>
                                  <v-list-item-title v-else>
                                    {{ profile.firstname + " " + profile.lastname}}
                                  </v-list-item-title>
                                  <v-list-item-subtitle>{{ profile.email }}
                                  </v-list-item-subtitle>
                                </v-list-item-content>
                                <div v-if="user.profile_id === authorId || user.permission_level > 0">
                                  <v-menu
                                          transition="slide-transition"
                                          bottom
                                          right
                                          :close-on-click="true"
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
                                      <v-list-item
                                              v-on:click="editUserActivityRole(item.tab.toLowerCase(), profile.email)">
                                        <div v-if="item.tab === 'Participants'">
                                          <v-list-item-title>Move to Organiser
                                          </v-list-item-title>
                                        </div>
                                        <div v-else>
                                          <v-list-item-title>Move to
                                            Participants
                                          </v-list-item-title>
                                        </div>
                                      </v-list-item>
                                    </v-card>
                                  </v-menu>
                                </div>
                              </v-list-item>
                            </div>
                          </v-card>
                        </div>
                      </v-tab-item>
                    </v-tabs-items>
                    <v-btn
                            height="40px" color="#1cca92"
                            id="activityPageMoreResultsButton"
                            outlined rounded
                            :loading="loadingParticipantsOrganisers"
                            v-on:click="getMoreResults()"
                    >More Results
                    </v-btn>
                  </v-card>

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
                      <div id="usersCard" class="activityPageCardDiv">
                        <v-card flat>
                          <v-list-item two-line v-for="user in sharedUsers" :key="user[0]">
                            <v-list-item-content>
                              <v-list-item-title v-if="user.middlename != null">
                                {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                              </v-list-item-title>
                              <v-list-item-title v-else>
                                {{ user.firstname + " " + user.lastname}}
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
        previewTabs: null,
        dialogTab: null,
        showMoreDialog: false,
        snackbar: false,
        timeout: 2000,
        errorMessage: "",
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
          defaultPage: 0, currentPage: 0, defaultSize: 8, currentSize: 8,
        },
        organisersPageInfo: {
          defaultPage: 0, currentPage: 0, defaultSize: 8, currentSize: 8,
        },
        userTabs: [
          {tab: 'Participants', content: [], preview: []},
          {tab: 'Organisers', content: [], preview: []},
        ],
        loadingParticipantsOrganisers: false,
        loadingParticipantsOrganisersDialog: false,
        sharedUsers: [],
        displaySharedUsersSuccessMsg: false,
        displaySharedUsersErrorMsg: false,
        sharedUsersStatusMsg: "",
        currentPage: 0,
        size: 10,
        bottom: false,
        watching: true,
        numFollowers: 0,
        numParticipants: 0,
        numOrganisers: 0,
        userOpttedIn: false,
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
    created: async function () {
      this.loadActivity();
      await this.getParticipants();
      await this.getOrganisers();
      this.getStats();
      this.checkUserHasOptedIn();
      return this.checkFollowing();
    },
    watch: {
      bottom(bottom) {
        if (bottom && this.watching) {
          this.currentPage += 1;
          apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(response => {
            if (response.data.content.length < this.size) {
              this.watching = false;
            }
            this.sharedUsers = this.sharedUsers.concat(response.data.content);
          })
        }
      }
    },
    methods: {
      // removed 'getActivityUpdates','getParticipants' and 'getOrganisers' for frontend test as they are not used
      ...mapActions(['updateUserDurationActivities', 'updateUserContinuousActivities']),

      checkUserHasOptedIn() {
        let i;
        for (i=0; i<this.userTabs[0].content.length; i++) {
          if (this.user.primary_email.localeCompare(this.userTabs[0].content[i].email) === 0) {
             this.userOpttedIn = true;
          }
        }
        if (!this.userOpttedIn) {
          for (let i=0; i<this.userTabs[1].content.length; i++) {
            if (this.user.primary_email.localeCompare(this.userTabs[1].content[i].email) === 0) {
              this.userOpttedIn = true;
            }
          }
        }
      },

      /**
       * Checks if user has scrolled to bottom of card code sourced from: https://codepen.io/mednabouli/pen/EdKzzL
       */
      bottomVisible() {
        const cardScroll = document.getElementById("usersCard");
        const scrollY = cardScroll.scrollHeight - cardScroll.scrollTop;
        const height = cardScroll.offsetHeight;
        const offset = height - scrollY;
        return offset === 0;
      },

      /**
       * Parses the list of emails the user entered by splitting them and removing any extra spaces. Checks each one is
       * valid by calling validateEmail, and displays an error message stating which email is invalid if any.
       */
      async parseEmails() {
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
              this.currentPage = 0;
              apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(response => {
                this.sharedUsers = response.data.content;
                this.watching = true;
              })
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
          this.userTabs[0].preview = this.userTabs[0].content.slice(0, 3);
        } catch (err) {
          console.error(err)
        }
      },
      async getStats() {
        await apiActivity.getActivityStats(this.$route.params.activityId).then(response => {
          this.numFollowers = response.data.followers;
          this.numOrganisers = response.data.organisers;
          this.numParticipants = response.data.participants;
        })
      },
      /**
       * Retrieves organisers for the activity
       */
      async getOrganisers() {
        try {
          let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.organisersPageInfo.currentPage, this.organisersPageInfo.currentSize);
          this.userTabs[1].content = response.data.content;
          this.userTabs[1].preview = this.userTabs[1].content.slice(0, 3);
        } catch (err) {
          console.error(err)
        }
      },
      /**
       * Retrieves more participants or organisers for the dialog box
       */
      async getMoreResults() {
        try {
          this.loadingParticipantsOrganisers = true;
          if (this.dialogTab === 0) {
            let response = await apiActivity.getParticipants(this.$route.params.activityId, this.participantsPageInfo.currentPage + 1, this.participantsPageInfo.currentSize);
            if (response.data.content === []) {
              this.errorMessage = "No more participants"
              this.snackbar = true;
            } else {
              this.userTabs[0].content = this.userTabs[0].content.concat(response.data.content);
              this.participantsPageInfo.currentPage += 1;
            }
            this.loadingParticipantsOrganisers = false;
          }
          if (this.dialogTab === 1) {
            let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.organisersPageInfo.currentPage + 1, this.organisersPageInfo.currentSize);
            if (response.data.content === []) {
              this.errorMessage = "No more organisers"
              this.snackbar = true;
            } else {
              this.userTabs[1].content = this.userTabs[1].content.concat(response.data.content);
              this.organisersPageInfo.currentPage += 1;
            }
            this.loadingParticipantsOrganisers = false;
          }
        } catch (err) {
          console.log(err);
        }
      },
      /**
       * Edits a user's role from participant to organiser and vice versa
       */
      async editUserActivityRole(role, email) {
        if (this.user.profile_id === this.authorId || this.user.permission_level > 0) {
          let newRole = "";
          if (role === "participants") newRole = "organiser"; else newRole = "participant";
          this.loadingParticipantsOrganisersDialog = true;
          await apiActivity.editUserActivityRole(this.user.profile_id, this.$route.params.activityId, newRole, email)
            .then(() => {
              this.showMoreDialog = false;
              this.getOrganisers();
              this.getParticipants();
            }).catch((err) => {
              console.log(err);
            })
          this.loadingParticipantsOrganisersDialog = false;
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
              apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(
                (response) => {
                  this.sharedUsers = response.data.content;
                  document.getElementById("usersCard").addEventListener('scroll', () => {
                    this.bottom = this.bottomVisible()
                  });
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
