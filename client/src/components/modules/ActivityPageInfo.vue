<template>
    <div class="activityInfo">
        <div id="activityTitle" class="activityTitle">{{ activityInfo.name }}</div>
        <div id="activityDescription" class="activityDescription">{{ activityInfo.description }}</div>
        <div id="activityLocation" class="activityLocation">{{ activityInfo.location }}</div>
        <div id="activityStartDate" class="activityStart" v-if="activityInfo.continuous === false"><h3> Start date: {{ startDate }}</h3></div>
        <div id="activityEndDate" class="activityEnd" v-if="activityInfo.continuous === false"><h3> End date: {{ endDate }}</h3></div>
        <div class="activityTypeTitle"><p>Activity Type: </p></div>
        <div id="activityTypeListing" class="activityTypes">
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

<style scoped>
    .activityInfo {
        font-family: 'Roboto';
        display: grid;
        grid-template-columns: auto 35%;
        grid-template-rows: 1fr 2fr 1fr 50px 50px;
        min-height: 300px;
        grid-template-areas: "Title Title" "Description Location" "Description Start" "ActivityTypesTitle End" "ActivityTypes Continuous";
    }

    .activityTitle {
        padding-left: 1rem;
        padding-top: 1rem;
        padding-bottom: 0.5rem;
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        color: #1cca92;
        font-size: 22px;
        grid-area: Title;
        font-weight: 500;

    }

    .activityDescription {
        display: flex;
        grid-area: Description;
        justify-content: center;
        align-items: center;
        text-align: center;
        padding-top: 1.5rem;
        padding-bottom: 2rem;
        padding-left: 1rem;
        padding-right: 0.5rem;
        font-size: 17px;
    }

    .activityLocation {
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        grid-area: Location;
        color: #6b6b6b;
        font-size: 17px;
    }

    .activityStart {
        display: flex;
        justify-content: center;
        align-items: flex-end;
        text-align: center;
        grid-area: Start;
        font-size: 12px;
        color: #6b6b6b;
        padding-right: 1rem;

    }

    .activityEnd {
        text-align: center;
        grid-area: End;
        font-size: 12px;
        padding-right: 1rem;
        color: #6b6b6b;

    }


    .activityTypeTitle{
        grid-area: ActivityTypesTitle;
        font-size: 15px;
        display: flex;
        justify-content: center;
        align-items: flex-end;
        text-align: center;
        font-weight: 500;
        colour: black;

    }

    .activityTypes {
        grid-area: ActivityTypes;
        text-align: center;
        font-size: 15px;
        color: #1cca92;
        font-weight: 500;

    }




</style>