<template>
    <div class="settingsContainer">
        <UserSettingsMenu/>
        <div class="settingsContentContainer">
            <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
                <button class="genericConfirmButton backButton">Back to Profile</button>
            </router-link>
            <h1>Change Password</h1>
            <hr>
            <div>
                <form @submit.prevent>
                    <input
                            v-if="user.permission_level === 0"
                            class="changePasswordInput"
                            type="password"
                            name="password"
                            placeholder="Current Password"
                            v-model="oldPassword"
                    />
                    <div class="errorMessageContainer">
                        <h6 class="updatePasswordErrorMessage" id="password_incorrect" hidden="true">Incorrect password</h6>
                    </div>
                    <input
                            class="changePasswordInput"
                            type="password"
                            name="newPassword"
                            placeholder="New Password"
                            v-model="newPassword"
                    />
                    <div class="errorMessageContainer">
                        <h6 class="updatePasswordErrorMessage" id="passwords_dont_match" hidden="true">Passwords must be matching</h6>
                    </div>
                    <input
                            class="changePasswordInput"
                            type="password"
                            name="confirmPassword"
                            placeholder="Re-enter Password"
                            v-model="confirmPassword"
                    />
                    <div class="errorMessageContainer">
                        <h6 class="updatePasswordErrorMessage" id="other_error" hidden="true"/>
                        <h6 class="updatePasswordSuccessMessage" id="success" hidden="true">Password successfully updated</h6>
                    </div>
                    <button class="genericConfirmButton updatePasswordButton" v-on:click="submitPasswordChange()" type="submit">Change Password</button>
                </form>
            </div>
        </div>
        <div class="floatClear"></div>
    </div>
</template>

<script>
import UserSettingsMenu from "./ProfileSettingsMenu";
import { apiUser } from "../../../api";
import { mapGetters } from "vuex";

export default {
  components: {
    UserSettingsMenu
  },
  data() {
    return {
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
      searchedUser: {}
    };
  },
  computed: {
    ...mapGetters(["user"]),
    /*
      Checks if the new password provided is a valid one.
    */
    validation() {
      return {
        oldPassword: this.oldPassword !== "" || this.user.permission_level > 0,
        match: this.newPassword == this.confirmPassword,
        length: /.{8,}/.test(this.newPassword),
        number: /\d/.test(this.newPassword),
        uppercase: /[A-Z]/.test(this.newPassword)
      };
    }
  },
  methods: {
    /*
      Uses user id from url to request user data.
    */
    async loadSearchedUser() {
      if (
        this.$route.params.profileId == null ||
        this.$route.params.profileId == ""
      ) {
        this.$router.push("/settings/password/" + this.user.profile_id);
        this.searchedUser = this.user;
      } else {
        var tempUserData = await apiUser.getUserById(
          this.$route.params.profileId
        );
        if (tempUserData == "Invalid permissions") {
          this.$router.push("/settings/password/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          this.searchedUser = tempUserData;
        }
      }
    },

    /*
      Hides error messages once valid data is provided.
    */
    hideErrorMessages() {
      document.getElementById("password_incorrect").hidden = true;
      document.getElementById("passwords_dont_match").hidden = true;
      document.getElementById("other_error").hidden = true;
    },

    /*
      Uses the validation function to check that the provided data is valid. If everything is valid, sends a
      request to the server side to update the password. Provides the user with appropriate error messages if
      the password change was unsuccessful.
    */
    submitPasswordChange() {
        document.getElementById("success").hidden = true;
      if (!this.isValid()) return;
      apiUser
        .changePassword(
          this.searchedUser.profile_id,
          this.oldPassword,
          this.newPassword,
          this.confirmPassword
        )
        .then(
          response => {
            this.hideErrorMessages();
            document.getElementById("success").hidden = false;
            console.log(response);
          },
          error => {
            const responseData = error.response.data;
            const responseCode = error.response.status;
            console.log(responseCode + ": " + responseData);

            if (
              responseCode === 400 &&
              responseData === "oldPassword is incorrect"
            ) {
              document.getElementById("password_incorrect").hidden = false;
              document.getElementById("passwords_dont_match").hidden = true;
            } else if (
              responseCode === 400 &&
              responseData === "newPassword and repeatPassword do no match"
            ) {
              document.getElementById("password_incorrect").hidden = true;
              document.getElementById("passwords_dont_match").hidden = false;
            } else {
              document.getElementById("password_incorrect").hidden = true;
              document.getElementById("passwords_dont_match").hidden = true;
              document.getElementById("other_error").hidden = false;
              document.getElementById("other_error").innerText =
                responseData.Errors;
            }
          }
        );
    },
    isValid() {
      if (!this.validation.oldPassword) {
        this.setError("Enter your old password");
        return false;
      }
      if (!this.validation.match) {
        this.setError("The repeated passwords do not match");
        return false;
      }
      if (!this.validation.length) {
        this.setError("Password length must at least be 8");
        return false;
      }
      if (!this.validation.number) {
        this.setError("Must contain at least one number.");
        return false;
      }
      if (!this.validation.uppercase) {
        this.setError("Must contain at least one uppercase character");
        return false;
      }
      return true;
    },
    setError(errorMessage) {
      this.hideErrorMessages();
      document.getElementById("other_error").hidden = false;
      document.getElementById("other_error").innerText = errorMessage;
    }
  },
  mounted() {
    this.loadSearchedUser();
  }
};
</script>
