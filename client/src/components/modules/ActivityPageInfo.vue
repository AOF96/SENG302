<template>
    <div class="activityInfoContainer">
        <div id="activityTitle" class="activityTitleLabel">{{ activityInfo.name }}</div>
        <div id="activityDescription" class="activityDescriptionLabel">{{ activityInfo.description }}</div>
        <div id="activityLocation" class="activityLocationLabel">{{ activityInfo.location }}</div>
        <div id="activityStartDate" class="activityStartLabel" v-if="activityInfo.continuous === false"><h3> Start date: {{ startDate }}</h3></div>
        <div id="activityEndDate" class="activityEndLabel" v-if="activityInfo.continuous === false"><h3> End date: {{ endDate }}</h3></div>
        <div class="activityTypeTitle"><p>Activity Type: </p></div>
        <div id="activityTypeListing" class="activityTypesContainer">
            <span v-for="a in activityInfo.activityTypes" :key="a.type_id">
                <span v-if="activityInfo.activityTypes.indexOf(a) != activityInfo.activityTypes.length - 1">
                        {{a.name}} ,
                </span>
                <span v-else>
                        {{a.name}}.
                </span>
            </span>
        </div>
    </div>
</template>

<script>
  export default {
    name: "ActivityPageInfo",
    props: ["activityInfo"],

    computed: {
      startDate() {
        return new Date(this.activityInfo.start).toLocaleString()
      },

      endDate() {
        return new Date(this.activityInfo.end).toLocaleString()
      }
    }
  }
</script>

<style>
    @import "../../../public/styles/pages/activities.css";
</style>