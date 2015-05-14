(ns cljs-bjj-tournament.competitors-csv
  (:require [cljs-bjj-tournament.model :refer [read-csv]]))

(def csv 
"Name,Belt,YOB,Weight,Club,Paid,Email,Age,Div,Weight group
Ryutaro Fujita,white,1980,60,UJC,no,,34,M1,Light
Tony Jones,White,1980,68,Silverdale BJJ,paid,tony.jones2011@hotmail.co.uk,34,M1,Light
Christian Quadros,White,1978,71,DS,Paid,,36,M2,Light
Joe Gallacher,White,1975,73,Zero Gravity,Paid,joemisabi@gmail.com,39,M2,Light
Gerhard Moerdyk,White,1981,75,ABJJ,Paid,,33,M1,Light
Parker Mason,White,1982,79,Tukaha,paid,parker@blogcampaigning.com,32,M1,Light
Altus Snyman,White,1984,80,DS,paid,,30,M1,Heavy
Malcom Burgess,White,1983,82,Tukaha,Paid,mal.burgess@gmail.com,31,M1,Heavy
Rhys Collier,White,1977,82,Tukaha,Paid,rhys.collier@gmail.com,37,M2,Heavy
Beez Ngarino Watt,White,1982,87,Tukaha,paid,ngarino.watt@gmail.com,32,M1,Heavy
Ari Panzer,White,1984,92,ABJJ,No,,30,M1,Light
Vini Leal,White,1982,96,ds hq,?,fb,32,M1,Light
Leon lockheart,White,1978,102,ABJJ,Paid,,36,M2,Heavy
John Turner,White,1968,73,ABJJ,No,,46,M3,
Darren Cantwell,White,1972,73,gravity bjj,Paid,darren@truckingrelief.co.nz,42,M3,
Tim Walker,Blue,1983,71,City BJJ,Paid,,31,M1,Light
Clinton Davies,Blue,1982,77,Tukaha,Paid,crpdavies@gmail.com,32,M1,Light
Joao Martins,Blue,1978,87,ABJJ,Paid,,36,M2,Light
Vahid Unesi,Blue,1977,91,Oliver MMA,Paid,,37,M2,Light
Stan Mataroa,Blue,1975,97,mma oliver south,?,fb,39,M2,Heavy
Pete Watts,Blue,1978,98,Submission Takapuna,Paid,wattscc@xtra.co.nz,36,M2,Heavy
Damon Kostidis,Blue,1977,98,ABJJ,No,,37,M2,Heavy
Joe Hicks,Blue,1975,110,mma oliver south,?,fb,39,M2,Heavy
Raphael Balao,Blue,1974,71,DS,Paid,,40,M3,Light
Khan Townsand-Parly,Blue,1971,73,UJC,No,,43,M3,Light
Honihana Reihana,Blue,1970,100,Clinch,Paid,mhkr@windowslive.com,44,M3,Heavy
Aidan Lovelock,Blue,1973,100,Oliver MMA,Paid,aidanl@tapper.co.n,41,M3,Heavy
Matthew Perkins,Blue,1970,102,GC BJJ,Paid,mattycperkins@gmail.com,44,M3,Heavy
Hira Young,Blue,1970,110,Rotorua BJJ,Paid,bjjrotorua@gmail.com,44,M3,Heavy
Linjian Chen,Blue,1964,64,ABJJ,No,,50,SUPER,
Ray Robertson,Blue,1956,70,UJC,No,,58,SUPER,")

(def competitors (read-csv csv))