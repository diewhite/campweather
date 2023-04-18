const time = document.querySelector('.time');
const tmp = document.querySelector('.tmp');
const pcp = document.querySelector('.pcp');
const vec = document.querySelector('.vec');
const wsd = document.querySelector('.wsd');
const container = document.querySelector('.container');
const notfound = document.querySelector('.not-found');
const button = document.querySelector('.search-box button');
const img = document.querySelector('.weather-box img');
const description = document.querySelector('.description');

button.addEventListener('click', () => {
		
		const data = document.querySelector('.search-box input').value;
		
		//함수 호출 시 초기화
		container.style.height = "145px";
		notfound.style.display='none';
		time.innerHTML = "";
		tmp.innerHTML = "";
		pcp.innerHTML = "";
		vec.innerHTML = "";
		wsd.innerHTML = "";
		description.innerHTML = "";

		//입력 데이터 없을 시 종료
		if (data === ''){
			return;
		}
		
		//입력한 주소의 날씨 조회(단기예보)
		fetch('/getWeather?searchAddr='+data)
			.then(response => response.json())
			.then(json=>{
		        if (json.error=="error"){
		        	console.log(json.error);
		        }
				var arr = json.response.body.items.item;
				var currentSKY;
				var currentPCP;
				var currentREH;
				var currentTMP;
				var currentWSD;
				var skyStatus;
				
				// 현재 시간 정보 가져오기
				const now = new Date();
				const seoulTimezoneOffset = 9 * 60;
				const seoulTime = new Date(now.getTime() + seoulTimezoneOffset * 60 * 1000);
				const currentHour = now.getHours();
				const currentDate = seoulTime.toISOString().slice(0, 4) + seoulTime.toISOString().slice(5, 7) + seoulTime.toISOString().slice(8, 10);
				console.log(now);
				console.log(currentHour);
				console.log(seoulTime.toISOString());
				console.log(currentDate);
				arr.forEach((obj,idx) => {
				
				//현재 정보 조회 및 출력
				if(currentDate==obj.fcstDate){
					if (parseInt(obj.fcstTime.substring(0, 2)) === currentHour){
						if(obj.category=='SKY'){
							currentSKY = obj.fcstValue;
							console.log("SKY:"+currentSKY);
						}
						if(obj.category=='PCP'){
							currentPCP = obj.fcstValue.slice(0, obj.fcstValue.length-2);
							console.log("PCP:"+currentPCP);
						}
						if(obj.category=='REH'){
							currentREH= obj.fcstValue;
							console.log("REH:"+currentREH);
							document.querySelector('.weather-details .humidity .text span').innerHTML = `${parseInt(obj.fcstValue)}%`;
						}
						if(obj.category=='TMP'){
							currentTMP= obj.fcstValue;
							console.log("TMP:"+currentTMP);
							document.querySelector('.temperature').innerHTML = `${parseInt(obj.fcstValue)}<span>℃</span>`;
						}
						if(obj.category=='WSD'){
							currentWSD= obj.fcstValue;
							console.log("WSD:"+currentWSD);
							document.querySelector('.weather-details .wind .text span').innerHTML = `${parseInt(obj.fcstValue)}m/s`;
						}
					}
				}
				
				//단기 정보 조회 및 출력
					if(obj.fcstTime%3==0){										//3시간 단위로 예보
						if(obj.category=='TMP'){								//기온
							var timeChild = document.createElement('td');
							timeChild.innerHTML = obj.fcstTime.substring(0, 2);
							time.appendChild(timeChild);
							
							var tmpChild = document.createElement('td');
							tmpChild.innerHTML = obj.fcstValue;
							tmp.appendChild(tmpChild);
						}
						if(obj.category=='PCP'){								//강수량
							var pcpChild = document.createElement('td');
							if(obj.fcstValue=="강수없음"){
								pcpChild.innerHTML = "0"
							}else{
								pcpChild.innerHTML = obj.fcstValue.slice(0, obj.fcstValue.length-2);								
							}
							pcp.appendChild(pcpChild);
						}
						if(obj.category=='VEC'){								//풍향
							var vecChild = document.createElement('td');
							vecChild.innerHTML = obj.fcstValue;
							vec.appendChild(vecChild);
						}
						if(obj.category=='WSD'){								//풍속
							var wsdChild = document.createElement('td');
							wsdChild.innerHTML = obj.fcstValue;
							if(obj.fcstValue>=5) wsdChild.style.backgroundColor = 'red';
							if(obj.fcstValue<5) wsdChild.style.backgroundColor = 'orange';
							if(obj.fcstValue<4) wsdChild.style.backgroundColor = 'green';
							if(obj.fcstValue<3)	wsdChild.style.backgroundColor = 'skyblue';
							wsd.appendChild(wsdChild);
						}
					}
				});

				// 하늘 상태 확인
				if(currentSKY>=0 & currentSKY<=5){
					skyStatus = "맑음";
				} else if (currentSKY>=6 & currentSKY<=8){
					skyStatus = "구름많음";
				} else {
					skyStatus = "흐림";
				}
				
				// 날씨 사진 변환 처리
				if(currentPCP=="강수"){									//강수량 없음
					if(currentHour>6 & currentHour<19){			//주간
						switch(skyStatus){
							case "맑음" :
								img.src="/images/sunny.jpg";
								description.innerHTML = "맑음";
							break;
								
							case "구름많음" :
								img.src="/images/sun_cloud.jpg";
								description.innerHTML = "구름많음";
							break;
							
							case "흐림" :
								img.src="/images/cloud.jpg";
								description.innerHTML = "흐림";
							break;

							default :
								img.src="";
								description.innerHTML = "";
							break;
						}
					} else {									//야간
						switch(skyStatus){
							case "맑음" :
								img.src="/images/night.jpg";
								description.innerHTML = "맑음";
							break;
							
							case "구름많음" :
							case "흐림" :
								img.src="/images/cloud_night.jpg";
								description.innerHTML = "흐림";
							break;

							default :
								img.src="";
								description.innerHTML = "";
							break;
						}
					}
				} else {										//강수량 있음
					if(currentTMP>0){
						img.src="/images/rainy.jpg";
						description.innerHTML = "비";
					} else {
						img.src="/images/snow.jpg";
						description.innerHTML = "눈";
					}
				}

				document.querySelector('.weather-box').style.scale="1";
				document.querySelector('.weather-box').style.opacity="1";
				document.querySelector('.weather-details').style.scale="1";
				document.querySelector('.weather-details').style.opacity="1";
				container.style.height = "1000px";
			})
			.catch(() =>{														//찾을 수 없는 주소인 경우 not found display
				document.querySelector('.weather-box').style.scale="0";
				document.querySelector('.weather-box').style.opacity="0";
				document.querySelector('.weather-details').style.scale="0";
				document.querySelector('.weather-details').style.opacity="0";
				notfound.style.display='inline';
				container.style.height = "550px";
			})
		});