<template>
  <div class="settingsNavContainer">
    <NavBar />
    <h1>Settings</h1>
    <div class="navButtonContainer">
      <router-link v-bind:to="'/settings/profile/' + this.$route.params.profileId">
        <div class="settingsNavButton">Profile</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/email/' + this.$route.params.profileId">
        <div class="settingsNavButton">Emails</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/password/' + this.$route.params.profileId">
        <div class="settingsNavButton">Password</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/passport_countries/' + this.$route.params.profileId">
        <div class="settingsNavButton">Passport Countries</div>
      </router-link>
      <hr />
      <router-link v-bind:to="'/settings/activities/' + this.$route.params.profileId">
        <div class="settingsNavButton">Activity Types</div>
      </router-link>
    </div>
  </div>
</template>

<script>
import NavBar from "../../modules/NavBar";
export default {
  components: {
    NavBar
  }
};
</script>

<style scoped>
@import "../../../../public/styles/pages/profileSettingsStyle.css";
</style>
