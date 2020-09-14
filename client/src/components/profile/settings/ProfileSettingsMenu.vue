<template>
  <div class="settingsNavContainer">
    <h1>Settings</h1>
    <div class="navButtonContainer" id="settingContainer">
      <router-link v-bind:to="'/settings/profile/' + this.$route.params.profileId">
        <div class="settingsNavButton">Profile</div>
      </router-link>
      <hr/>
      <router-link v-bind:to="'/settings/user_location/' + this.$route.params.profileId">
        <div class="settingsNavButton" id="navigateToLocationSettings">Location</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/email/' + this.$route.params.profileId">
        <div class="settingsNavButton" id="navigateToEmailSettings">Emails</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/password/' + this.$route.params.profileId">
        <div class="settingsNavButton" id="navigateToPasswordSettings">Password</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/passport_countries/' + this.$route.params.profileId">
        <div class="settingsNavButton" id="navigateToPassportSettings">Passport Countries</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/activities/' + this.$route.params.profileId">
        <div class="settingsNavButton" id="navigateToActivityTypesSettings">Activity Types</div>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name:"ProfileSettingsMenu",
  components: {
    //
  }
};
</script>

<style scoped>
@import "../../../../public/styles/pages/profileSettingsStyle.css";
</style>
