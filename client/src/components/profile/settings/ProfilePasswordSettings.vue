<template>
    <div class="settingsContainer">
        <UserSettingsMenu/>
        <div class="settingsContentContainer">
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

                    <input
                            class="changePasswordInput"
                            type="password"
                            name="newPassword"
                            placeholder="New Password"
                            v-model="newPassword"
                    />

                    <input
                            class="changePasswordInput"
                            type="password"
                            name="confirmPassword"
                            placeholder="Re-enter Password"
                            v-model="confirmPassword"
                    />

                    <button class="genericConfirmButton updatePasswordButton" v-on:click="submitPasswordChange()" type="submit">Save New Password</button>
                </form>
            </div>
        </div>
        <div class="floatClear"></div>
        <v-snackbar
            v-model="snackbar"
            :color="snackbarColour"
            top
        >
            {{ snackbarText }}
            <v-btn
                @click="snackbar = false"
                color="white"
                text
            >
                Close
            </v-btn>
        </v-snackbar>
    </div>
</template>

<script>
import UserSettingsMenu from "./ProfileSettingsMenu";
import { mapGetters, mapActions } from "vuex";
const SUCCESS = "success";
const ERROR = "error";

export default {
  components: {
    UserSettingsMenu
  },
  data() {
    return {
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
      searchedUser: {},
      snackbar: false,
      snackbarText: null,
      snackbarColour: '',
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
        match: this.newPassword === this.confirmPassword,
        length: /.{8,}/.test(this.newPassword),
        number: /\d/.test(this.newPassword),
        uppercase: /[A-Z]/.test(this.newPassword),
        lowercase: /[a-z]/.test(this.newPassword)
      };
    }
  },
  methods: {
      ...mapActions(["getUserById", "changePassword"]),
    /*
      Uses user id from url to request user data.
    */
    async loadSearchedUser() {
      if (
        this.$route.params.profileId === null ||
        this.$route.params.profileId === ""
      ) {
        this.$router.push("/settings/password/" + this.user.profile_id);
        this.searchedUser = this.user;
      } else {
        var tempUserData = await this.getUserById(
          this.$route.params.profileId
        );
        if (tempUserData === "Invalid permissions") {
          this.$router.push("/settings/password/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          this.searchedUser = tempUserData;
        }
      }
    },

    /*
      Uses the validation function to check that the provided data is valid. If everything is valid, sends a
      request to the server side to update the password. Provides the user with appropriate error messages if
      the password change was unsuccessful.
    */
    submitPasswordChange() {
      if (!this.isValid()) return;
      this.changePassword( {
          'id': this.searchedUser.profile_id,
          'oldPassword': this.oldPassword,
          'newPassword': this.newPassword,
          'confirmPassword': this.confirmPassword }
        )
        .then(
          response => {
            this.showSnackbar("Password updated.", SUCCESS);
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
              this.showSnackbar("Incorrect password", ERROR);
            } else if (
                responseCode === 400 &&
                responseData === "newPassword and repeatPassword do no match"
            ) {
                this.showSnackbar("Passwords must be matching", ERROR);
            } else {
                this.showSnackbar(responseData.Errors[0], ERROR);
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
        if (!this.validation.lowercase) {
            this.setError("Must contain at least one lowercase character");
            return false;
        }
      return true;
    },
    setError(errorMessage) {
        this.showSnackbar(errorMessage, ERROR);
    },

  /**
   * Shows a pop-up message to the user displaying the result of a function call.
   * @param text: the text to be displayed to the user.
   * @param colour: the colour to be displayed with the snackbar.
   */
  showSnackbar(text, colour) {
      this.snackbarText = text;
      this.snackbarColour = colour;
      this.snackbar = true;
  }

  },
  mounted() {
      if (!this.user.isLogin) {
          this.$router.push('/login');
      } else {
          this.loadSearchedUser();
      }
  }
};
</script>
