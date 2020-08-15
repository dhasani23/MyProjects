//generate the y-axis data for the plots on the chart
function makeChart(interestRate, compoundingFrequency) {

    interestRate = 1 + (interestRate/100);
    var exponent = compoundingFrequency/12;
    var balances = [];
    var year = 0;
    //get user input
    var principal = parseInt(document.getElementById('textarea1').value,10);
    var monthlyPay = parseInt(document.getElementById('textarea2').value,10);

    while (balances.length != 56) { //populate balances with compount interest formula + monthly contributions
      balances[year] = ((principal*(interestRate**(compoundingFrequency*year))) + monthlyPay*(((interestRate**(compoundingFrequency*year))-1)/(((interestRate**exponent)-1)))).toFixed(2);
      year++;
    }

    return balances;

}