// generate the y-axis data for the plots on the chart
// isInvesting is true if the account selected is an investing option (rather than saving)
// riskLevel is a value from 0-5 inclusive used to determine the frequency & severity of the economic downturns to graph
function makeChart(interestRate, compoundingFrequency, isInvesting, riskLevel) {

    interestRate = 1 + (interestRate/100);
    var exponent = compoundingFrequency/12;
    var balances = [];
    var year = 0;
    //get user input
    var principal = parseInt(document.getElementById('textarea1').value,10);
    var monthlyPay = parseInt(document.getElementById('textarea2').value,10);

    while (balances.length != 56) { // populate balances with compount interest formula + monthly contributions
      balances[year] = ((principal*(interestRate**(compoundingFrequency*year))) + monthlyPay*(((interestRate**(compoundingFrequency*year))-1)/(((interestRate**exponent)-1)))).toFixed(2);
      year++;
    }

    if (isInvesting) {

        // used for the random # generation:
        var bottomOfRange = 0; // represents bottom of range for # of declines
        var delta = 0;        //  represents the difference between maximum and minimum declines (ex. case 3: = 30% [minus] 5% = 25)
        var upperBound = 0;  //   represents the maximum decline (ex. case 1: = 90 means maximum of a 10% decline in portfolio value possible)

        switch (riskLevel) {
            // 6-10 downturns; 5-10% decline for each
            case 1:
                bottomOfRange = 6;
                delta = 5;
                upperBound = 90;
                break;
            // 10-14 downturns; 5-20% decline for each
            case 2:
                bottomOfRange = 10;
                delta = 15;
                upperBound = 80;
                break;
            // 14-18 downturns; 5-30% decline for each
            case 3:
                bottomOfRange = 14;
                delta = 25;
                upperBound = 70;
                break;
             // 18-22 downturns; 5-40% decline for each
            case 4:
                bottomOfRange = 18;
                delta = 35;
                upperBound = 60;
                break;
             // 22-26 downturns; 5-50% decline for each
            case 5:
                bottomOfRange = 22;
                delta = 45;
                upperBound = 50;
                break;
        }

        // the value 5 represents the fact that each interval in the number of downturns is of width = 4 (remember Math.random() is exclusive of upper bound!)
        var numDeclines = Math.floor(Math.random() * 5 + bottomOfRange); // pick number of declines ranging from 3-11 depending on risk level chosen
        var yearsToDecline = []
        for (var i = 0; i < numDeclines; i++) {
            yearsToDecline[i] = Math.floor(Math.random() * 46) + 2025; // pick years to decline at random, b/w 2025-2070
        }
        // update values in balances to account for the economic declines (these could represent recessions or other fluctuations)
        for (var i = 0; i < yearsToDecline.length; i++) {
            // add 1 to delta so that interval is inclusive and divide by 100 to convert to a decimal value for multiplication
            // ex. case 2: the portfolio balance for each of 5-7 random years is decreased by a random value b/w 5% and 20% (i.e. multiply by 0.95 to 0.80)
            var percentChange = ((Math.random()*(delta+1) + upperBound) / 100);
            // update portfolio balance to be some fraction of the previous year's balance
            balances[yearsToDecline[i] - 2020] = balances[yearsToDecline[i] - 2020 - 1] * percentChange;
        }
    }
    return balances;
}
