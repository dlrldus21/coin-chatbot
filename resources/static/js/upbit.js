const request = new XMLHttpRequest();
const url = 'https://api.upbit.com/v1/market/all';

request.open("GET", url, false);
request.send();
var obj = JSON.parse(request.responseText);
console.log(obj);

const request = new XMLHttpRequest();
const url = 'https://api.upbit.com/v1/ticker?markets=KRW-BTC';;

request.open("GET", url, false);
request.send();
var obj = JSON.parse(request.responseText);
console.log(obj);

const request = new XMLHttpRequest();
const url = 'https://api.upbit.com/v1/candles/minutes/5?market=KRW-BTC&count=3';

request.open("GET", url, false);
request.send();
var obj = JSON.parse(request.responseText);
console.log(obj);

function MarketCoinRead(url){
    const request = new XMLHttpRequest();
    request.open("GET", url, false);
    request.send();
    var obj = JSON.parse(request.responseText);
    return obj;
}

MarketCoinList();
function MarketCoinList(){
    const url = 'https://api.upbit.com/v1/market/all';
    var obj = MarketCoinRead(url);
    console.log(obj);
}

MarketCoinTicker('KRW','BTC');
function MarketCoinTicker(market,coin){
    const url = 'https://api.upbit.com/v1/ticker?markets='+market+'-'+coin;;
    var obj = MarketCoinRead(url);
    console.log(obj);
}

MarketCoincandlesMinutes('KRW','STEEM',5,3);
function MarketCoincandlesMinutes(market,coin,timeVal,count){
    const url = 'https://api.upbit.com/v1/candles/minutes/'+timeVal+'?market='+market+'-'+coin+'&count='+count;
    var obj = MarketCoinRead(url);
    console.log(obj);
}