(ns cljs-bjj-tournament.competitors-csv
  (:require [cljs-bjj-tournament.model :refer [read-csv]]))

(def csv 
"Name,Belt,YOB,Weight,Club,Paid,Email,Age,Div
Parker Mason,White,1982,79,Tukaha,tbc,parker@blogcampaigning.com,32,M1
Gerhard Moerdyk,White,1981,75,ABJJ,Paid,,33,M1
Altus Snyman,White,1984,80,DS,paid,,30,M1
Malcom Burgess,White,1983,82,Tukaha,Paid,mal.burgess@gmail.com,31,M1
Ravi Chandiramani,White,1983,85,Clinch,Paid,ravic254@gmail.com,31,M1
Beez Ngarino Watt,White,1982,87,Tukaha,TBC,ngarino.watt@gmail.com,32,M1
Dwain Hindriksen,White,1983,91,UJC,No,,31,M1
Ari Panzer,White,1984,92,ABJJ,No,,30,M1
Sheldon Edwards,White,1983,94,ABJJ,No,,31,M1
Tim Walker,Blue,1983,71,City BJJ,Paid,,31,M1
Dieo Chambriere,Blue,1981,64,ABJJ,No,,33,M1
Clinton Davies,Blue,1982,77,Tukaha,Paid,crpdavies@gmail.com,32,M1
Christian Quadros,White,1978,71,DS,Paid,,36,M2
Joe Gallacher,White,1975,73,Zero Gravity,Paid,joemisabi@gmail.com,39,M2
Rhys Collier,White,1977,82,Tukaha,Paid,rhys.collier@gmail.com,37,M2
Leon lockheart,White,1978,102,ABJJ,NO,,36,M2
Louie Lim,White,1979,75,Tukaha,Paid,,35,M2
Pete Watts,Blue,1978,98,Submission Takapuna,Paid,wattscc@xtra.co.nz,36,M2
Marc Tai,Blue,1978,68,ABJJ,No,,36,M2
Joao Martins,Blue,1978,87,ABJJ,No,,36,M2
Damon Kostidis,Blue,1977,98,ABJJ,No,,37,M2
Vahid Unesi,Blue,1977,91,oliver mma,TBC,,37,M2
John Turner,White,1969,73,ABJJ,No,,46,M3
Honihana Reihana,Blue ,1970,100,Clinch,Paid,mhkr@windowslive.com,44,M3
Aidan Lovelock,Blue ,1973,100,Oliver MMA,Paid,aidanl@tapper.co.n,41,M3
Raphael Balao,Blue,1974,71,DS,Paid,,40,M3
Khan Townsand-Parly,Blue,1971,73,UJC,No,,43,M3
Linjian Chen,Blue,1964,64,ABJJ,No,,50,SUPER
Ray Robertson,Blue,1956,70,UJC,No,,58,SUPER
Hira Young,Blue,1970,110,Rotorua BJJ,Paid,bjjrotorua@gmail.com,44,M3
Tony Jones,tb,tbc,tbc,Silverdale BJJ,tbc,tony.jones2011@hotmail.co.uk,,
Matthew Perkins,Blue,1971,102,GC BJJ,tbc,mattycperkins@gmail.com,44,M3")

(def competitors (read-csv csv))