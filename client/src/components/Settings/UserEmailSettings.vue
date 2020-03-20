<template>
<div id="settingsWrap">
  <UserSettingsMenu />
  <div class="settingsContent">
    <h1>Edit Email Settings</h1>
    <hr>
    <h3>Primary email</h3>
    <div class="emailBlock">
      <h4>{{ primary_email }}</h4>
    </div>
    <h3>Secondary emails:</h3>
    <div class="signup-row">
      <h6 class="email_error" id="email_exists" hidden="true">Email already exists</h6>
      <h6 class="email_success" id="email_added" hidden="true">Email added successfully</h6>
      <h6 class="email_success" id="email_swapped" hidden="true">Emails swapped successfully</h6>
      <h6 class="email_success" id="email_deleted" hidden="true">Email deleted successfully</h6>
      <h6 class="email_success" id="email_edited" hidden="true">Email edited successfully</h6>
    </div>
    <div class="emailBlock emailSecondary" v-for="email in secondary_emails" v-bind:key="email">
      <h4 v-on:click="openEmailEditBox(email)">{{email}}</h4>
      <svg v-on:click="openEmailEditBox(email)" class="editIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
        <path d="M0 0h24v24H0z" fill="none" /></svg>
      <button class="setPrimaryButton" v-on:click="updatePrimaryEmail(email)">Set Primary</button>
      <svg class="removeEmailButton" v-on:click="removeEmail(email)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
        <path d="M0 0h24v24H0z" fill="none" /></svg>
    </div>
    <!-- <div v-if="secondary_emails.length < 4"> -->
      <form @submit.prevent>
        <input id="addEmailInput" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
        <button id="addEmailButton" v-if="showButton" v-on:click="addEmail(textInput)">Add</button>
      </form>
    <!-- </div> -->
  </div>
  <transition name="fade">
    <div id="cover" v-if="showEditBox" @click="showEditBox = false"></div>
  </transition>
  <transition name="fadeup">
    <div class="floatingBox" id="editEmailBox" v-if="showEditBox">
      <h1>Edit Email</h1>
      <form @submit.prevent>
        <input id="editEmailInput" v-model="editEmailInput" type="email" placeholder="Secondary Email" required>
        <button id="editEmailButton" v-on:click="editEmail()">Update</button>
      </form>
    </div>
  </transition>
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu';
import {apiUser} from "../../api";
import router from "../../router";
const LIMIT_NUM_EMAIL = 4;

export default {
  components: {
    UserSettingsMenu
  },
  data() {
    return {
      primary_email: "",
      secondary_emails: [],
      showButton: true,
      textInput: '',
      newEmail: '',
      limit_num_email: LIMIT_NUM_EMAIL,
      tempOldEmail: '',
      editEmailInput: '',
      showEditBox: false,
    }
  },
  computed: {
    ...mapState(['user'])
  },
  methods: {
    ...mapActions(['updateUserEmail']),
    ...mapActions(['logout']),

    hideAllMessages() {
      document.getElementById("email_exists").hidden = true;
      document.getElementById("email_added").hidden = true;
      document.getElementById("email_swapped").hidden = true;
      document.getElementById("email_deleted").hidden = true;
      document.getElementById("email_edited").hidden = true;

    },

    // Loads the email values from the user object into the edit email page elements
    loadEmailsIntoElements() {
      this.primary_email = this.user.user.primary_email;
      this.secondary_emails = this.user.user.additional_email;
    },

    /*
           Adds a new email into the secondary emails lists. Prevents the user from entering empty text or from trying to
           enter an existing email.
        */
    async addEmail(textInput) {
      const emails = await apiUser.getAllEmails();
      if (this.secondary_emails.includes(textInput) || textInput == this.user.user.primary_email || emails.data.includes(textInput)) {
        this.hideAllMessages();
        document.getElementById("email_exists").hidden = false;
      } else if (textInput != "" && (/[^\s]+@[^\s]+/.test(textInput))) {
        await apiUser.addEmails(this.user.user.profile_id, this.user.user.primary_email, this.user.user.secondary_emails).then((response) => {
          this.secondary_emails.push(this.textInput);
          this.updateUserEmail(this);
          var tempThis = this;
          setTimeout(function () {
            tempThis.textInput = "";
          }, 10);
          this.hideAllMessages();
          document.getElementById("email_added").hidden = false;
          this.updateUserProfile(this.user);
          console.log(response);
        }, (error) => {
          this.logout();
          router.push('login');
          alert('An error occurred. Please log back in');
          console.log(error);
        });
      }
    },

    /*
         Function that updates the primary email of an user by picking one from the secondary emails list and adding the
         previous primary email to the secondary list.
         */
    updatePrimaryEmail(secondaryEmail) {
      const index = this.secondary_emails.indexOf(secondaryEmail);
      apiUser.editEmail(this.user.user.profile_id, this.user.user.primary_email, this.user.user.secondary_emails).then((response) => {
        if (index > -1) {
          this.secondary_emails.splice(index, 1);
        }
        this.secondary_emails.push(this.primary_email);
        this.primary_email = secondaryEmail;
        this.updateUserEmail(this);
        this.hideAllMessages();
        document.getElementById("email_swapped").hidden = false;
        this.updateUserProfile(this.user);
        console.log(response);
      }, (error) => {
        this.logout();
        router.push('login');
        alert('An error occurred. Please log back in');
        console.log(error);
      });
    },

    openEmailEditBox(secondaryEmail) {
      this.tempOldEmail = secondaryEmail;
      this.editEmailInput = secondaryEmail;
      this.showEditBox = true;
    },

    async editEmail() {
      const emails = await apiUser.getAllEmails();
      if (this.secondary_emails.includes(this.editEmailInput) || this.editEmailInput == this.primary_email || emails.data.includes(this.editEmailInput)) {
        alert("Email already in use.");
      } else if (/[^\s]+@[^\s]+/.test(this.editEmailInput)) {
        apiUser.editEmail(this.user.user.profile_id, this.user.user.primary_email, this.user.user.secondary_emails).then((response) => {
          const index = this.secondary_emails.indexOf(this.tempOldEmail);
          this.secondary_emails[index] = this.editEmailInput;
          this.showEditBox = false;
          this.updateUserEmail(this);
          this.hideAllMessages();
          document.getElementById("email_edited").hidden = false;
          this.updateUserProfile(this.user);
          console.log(response);
        }, (error) => {
          this.logout();
          router.push('login');
          alert('An error occurred. Please log back in');
          console.log(error);
        });
      }
    },

    /* Function that removes an email from the secondary emails list */
    removeEmail(email) {
      apiUser.editEmail(this.user.user.profile_id, this.user.user.primary_email, this.user.user.secondary_emails).then((response) => {
        if (this.secondary_emails.length === 1) {
          this.secondary_emails = []
        } else {
          const index = this.secondary_emails.indexOf(email);
          if (index > -1) {
            this.secondary_emails.splice(index, 1);
          }
        }
        if (this.secondary_emails.length > 0) {
          this.showButton = true;
        }
        this.updateUserEmail(this);
        this.hideAllMessages();
        document.getElementById("email_deleted").hidden = false;
        this.updateUserProfile(this.user);
        console.log(response);
      }, (error) => {
        this.logout();
        router.push('login');
        alert('An error occurred. Please log back in');
        console.log(error);
      });
    },
    mounted() {
      this.loadEmailsIntoElements();
    }
  }
}
</script>
