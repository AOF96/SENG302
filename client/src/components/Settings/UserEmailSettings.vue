<template>
<div id="settingsWrap">
  <UserSettingsMenu />
  <div class="settingsContent">
    <h1>Edit Email Settings</h1>
    <hr>
    <h3>Primary email</h3>
    <div class="emailBlock">
      <h4>{{primary_email}}</h4>
    </div>
    <h3>Secondary emails:</h3>
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
    <div v-if="this.secondary_emails.length < 4">
      <form @submit.prevent>
        <input id="addEmailInput" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
        <button id="addEmailButton" v-if="showButton" v-on:click="addEmail(textInput)">Add</button>
      </form>
    </div>
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
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import {userInfo} from "../../globals";
import {apiUser} from "../../api";

export default {
  components: {
    UserSettingsMenu
  },
  data() {
    return {
      primary_email: userInfo.email,
      secondary_emails: userInfo.secondaryEmails,
      showButton: true,
      textInput: '',
      newEmail: '',
      tempOldEmail: '',
      editEmailInput: '',
      showEditBox: false
    }
  },
  methods: {
    updateGlobals() {
      userInfo.email = this.primary_email;
      userInfo.secondaryEmails = this.secondary_emails;
    },

    /*
       Adds a new email into the secondary emails lists. Prevents the user from entering empty text or from trying to
       enter an existing email.
    */
    addEmail(textInput) {
      if (this.secondary_emails.includes(textInput) || textInput == this.primary_email) {
        alert("Please enter an email that has not been used before");
      } else if (textInput != "" && (/[^\s]+@[^\s]+/.test(textInput))) {
        this.secondary_emails.push(this.textInput);
        this.updateGlobals();
        var tempThis = this;
        setTimeout(function() {
            tempThis.textInput = "";
        }, 10);
        apiUser.addEmails(userInfo.profileId, this.primary_email, this.secondary_emails);
      }
    },

    /*
     Function that updates the primary email of an user by picking one from the secondary emails list and adding the
     previous primary email to the secondary list.
     */
    updatePrimaryEmail(secondaryEmail) {
      const index = this.secondary_emails.indexOf(secondaryEmail);
      if (index > -1) {
        this.secondary_emails.splice(index, 1);
      }
      this.secondary_emails.push(this.primary_email);
      this.primary_email = secondaryEmail;
      this.updateGlobals();
      apiUser.editEmail(userInfo.profileId, this.primary_email, this.secondary_emails);
    },

    openEmailEditBox(secondaryEmail) {
      this.tempOldEmail = secondaryEmail;
      this.editEmailInput = secondaryEmail;
      this.showEditBox = true;
    },

    editEmail() {
      if (this.secondary_emails.includes(this.editEmailInput) || this.editEmailInput == this.primary_email) {
        alert("Please enter an email that has not been used before.");
      } else if (/[^\s]+@[^\s]+/.test(this.editEmailInput)) {
        const index = this.secondary_emails.indexOf(this.tempOldEmail);
        this.secondary_emails[index] = this.editEmailInput;
        this.showEditBox = false;
        apiUser.editEmail(userInfo.profileId, this.editEmailInput);
      }
    },

    /* Function that removes an email from the secondary emails list */
    removeEmail(email) {
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
      this.updateGlobals();
      apiUser.editEmail(userInfo.profileId, this.primary_email, this.secondary_emails);
    }
  }
}
</script>
