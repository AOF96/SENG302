export default {

  //from https://css-tricks.com/everything-you-need-to-know-about-date-in-javascript/

  getFormatDate(dateObject) {

    const months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ]

    const days = [
      'Sun',
      'Mon',
      'Tue',
      'Wed',
      'Thu',
      'Fri',
      'Sat'
    ]

    const year = dateObject.getFullYear()
    const date = dateObject.getDate()
    const monthIndex = dateObject.getMonth()
    const monthName = months[monthIndex]
    const dayIndex = dateObject.getDay()
    const dayName = days[dayIndex]


    //from https://stackoverflow.com/questions/25275696/javascript-format-date-time/25276435

    let hours = dateObject.getHours()
    let  minutes = dateObject.getMinutes()
    let ampm = hours >= 12 ? 'pm' : 'am';
    hours = hours % 12;
    hours = hours ? hours : 12; // hours 0 should be 12
    minutes = minutes < 10 ? '0'+minutes : minutes;
    let formatTime = hours + ':' + minutes + ' ' + ampm;

    const formatDate = `${dayName}, ${date} ${monthName} ${year} ${formatTime}`

    return formatDate
  }


}