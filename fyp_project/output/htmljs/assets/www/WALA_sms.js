<Code body of function L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/convertPhoneToArray>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..3]
    -> BB4
    -> BB2
BB2[4..8]
    -> BB3
    -> BB18
BB3[9..12]
    -> BB5
BB4[13..13]
    -> BB5
BB5[14..14]
    -> BB8
    -> BB6
BB6[15..19]
    -> BB7
    -> BB18
BB7[20..20]
    -> BB8
BB8[21..24]
    -> BB9
    -> BB18
BB9[25..27]
    -> BB10
    -> BB18
BB10[28..30]
    -> BB11
    -> BB18
BB11[31..33]
    -> BB12
    -> BB18
BB12[34..35]
    -> BB17
    -> BB13
BB13[36..37]
    -> BB14
    -> BB18
BB14[38..38]
    -> BB15
    -> BB18
BB15[39..39]
    -> BB16
    -> BB18
BB16[40..40]
    -> BB17
BB17[41..41]
    -> BB18
BB18[-1..-2]
Instructions:
BB0
BB1
0   v4 = new <JavaScriptLoader,LArray>@0     sms.js [145->409] (line 8) {4=[arguments]}
1   v6 = typeof(v3)                          sms.js [191->203] (line 9) {3=[phone, $$destructure$rcvr4]}
2   v5 = binaryop(strict_eq) v6 , v7:#string sms.js [191->216] (line 9)
3   conditional branch(eq) v5,v9:#0          sms.js [191->245] (line 9)
BB2
8   v15 = dispatch v14:#indexOf@8 v3,v16:#, exception:v17sms.js [220->238] (line 9) {14=[$$destructure$elt3], 3=[phone, $$destructure$rcvr4]}
BB3
9   v18 = minus v19:#1.0                     sms.js [243->245] (line 9)
10   v10 = binaryop(strict_ne) v15 , v18     sms.js [220->245] (line 9)
12   goto (from iindex= 12 to iindex = 14)   sms.js [191->245] (line 9)
BB4
BB5
           v8 = phi  v10,v5
14   conditional branch(eq) v8,v9:#0         sms.js [187->288] (line 9)
BB6
19   v23 = dispatch v22:#split@19 v3,v16:#, exception:v24sms.js [265->281] (line 10) {23=[phone, $$destructure$rcvr4], 22=[$$destructure$elt4], 3=[phone, $$destructure$rcvr4]}
BB7
BB8
           v12 = phi  v3,v23
23   v31 = global:global Object              sms.js [297->303] (line 12)
24   check v31                               sms.js [297->303] (line 12)
BB9
26   v13 = prototype_values(v31)             sms.js [297->313] (line 12)
27   v29 = getfield < JavaScriptLoader, LRoot, prototype, <JavaScriptLoader,LRoot> > v13sms.js [297->313] (line 12)
BB10
29   v20 = prototype_values(v29)             sms.js [297->322] (line 12)
30   v28 = getfield < JavaScriptLoader, LRoot, toString, <JavaScriptLoader,LRoot> > v20sms.js [297->322] (line 12) {28=[$$destructure$rcvr5]}
BB11
33   v37 = dispatch v36:#call@33 v28,v12 exception:v38sms.js [297->334] (line 12) {36=[$$destructure$elt5], 28=[$$destructure$rcvr5], 12=[phone, $$destructure$rcvr4]}
BB12
34   v25 = binaryop(strict_ne) v37 , v39:#[object Array]sms.js [297->355] (line 12)
35   conditional branch(eq) v25,v9:#0        sms.js [293->389] (line 12)
BB13
36   v42 = global:global Array               sms.js [375->382] (line 13)
37   check v42                               sms.js [375->382] (line 13)
BB14
38   v40 = construct v42@38 exception:v43    sms.js [375->382] (line 13) {40=[phone, $$destructure$rcvr4]}
BB15
39   fieldref v40.v44:#0 = v12 = v12         sms.js [375->382] (line 13) {40=[phone, $$destructure$rcvr4], 12=[phone, $$destructure$rcvr4]}
BB16
BB17
           v21 = phi  v12,v40
41   return v21                              sms.js [394->407] (line 15) {21=[phone, $$destructure$rcvr4]}
BB18
<Code body of function L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..8]
    -> BB2
    -> BB8
BB2[9..9]
    -> BB3
    -> BB8
BB3[10..12]
    -> BB4
    -> BB8
BB4[13..13]
    -> BB5
    -> BB8
BB5[14..17]
    -> BB6
    -> BB8
BB6[18..20]
    -> BB7
    -> BB8
BB7[21..21]
    -> BB8
BB8[-1..-2]
Instructions:
BB0
BB1
0   v6 = new <JavaScriptLoader,LArray>@0     sms.js [41->1536] (line 1) {6=[arguments]}
1   v8 = global:global $$undefined           sms.js [93->128] (line 4)
2   lexical:exec@L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41 = v8sms.js [93->128] (line 4)
3   v10 = global:global $$undefined          sms.js [130->143] (line 6) {10=[sms]}
5   v15 = global:global Function             sms.js [41->1536] (line 1)
6   v12 = construct v15@6 v14:#L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/convertPhoneToArray exception:v13sms.js [41->1536] (line 1) {12=[convertPhoneToArray]}
7   v19 = global:global __WALA__int3rnal__globalsms.js [104->127] (line 4)
8   check v19                                sms.js [104->127] (line 4)
BB2
9   v17 = invoke v3@9 v19,v20:#cordova/exec exception:v21sms.js [104->127] (line 4) {3=[require]}
BB3
10   lexical:exec@L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41 = v17sms.js [93->128] (line 4)
11   v24 = global:global Object              sms.js [140->142] (line 6)
12   check v24                               sms.js [140->142] (line 6)
BB4
13   v22 = construct v24@13 exception:v25    sms.js [140->142] (line 6) {22=[sms]}
BB5
15   v30 = global:global Function            sms.js [412->1350] (line 19)
16   v27 = construct v30@16 v29:#L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@423 exception:v28sms.js [412->1350] (line 19)
17   fieldref v22.v33:#send = v27 = v27      sms.js [412->1350] (line 19) {22=[sms]}
BB6
18   v37 = global:global Function            sms.js [1353->1510] (line 46)
19   v34 = construct v37@19 v36:#L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@1373 exception:v35sms.js [1353->1510] (line 46)
20   fieldref v22.v38:#hasPermission = v34 = v34sms.js [1353->1510] (line 46) {22=[sms]}
BB7
21   fieldref v5.v39:#exports = v22 = v22    sms.js [1513->1533] (line 56) {5=[module], 22=[sms]}
BB8
<Code body of function L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@1373>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..2]
    -> BB2
    -> BB6
BB2[3..4]
    -> BB3
    -> BB6
BB3[5..6]
    -> BB4
    -> BB6
BB4[7..7]
    -> BB5
    -> BB6
BB5[8..8]
    -> BB6
BB6[-1..-2]
Instructions:
BB0
BB1
0   v5 = new <JavaScriptLoader,LArray>@0     sms.js [1373->1510] (line 46) {5=[arguments]}
1   v8 = lexical:exec@L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41sms.js [1418->1422] (line 48)
2   check v8                                 sms.js [1418->1422] (line 48)
BB2
3   v10 = global:global __WALA__int3rnal__globalsms.js [1418->1507] (line 48)
4   check v10                                sms.js [1418->1507] (line 48)
BB3
5   v15 = global:global Array                sms.js [1499->1501] (line 52)
6   check v15                                sms.js [1499->1501] (line 52)
BB4
7   v13 = construct v15@7 exception:v16      sms.js [1499->1501] (line 52)
BB5
8   v6 = invoke v8@8 v10,v3,v4,v11:#Sms,v12:#has_permission,v13 exception:v17sms.js [1418->1507] (line 48) {3=[success], 4=[failure]}
BB6
<Code body of function L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@423>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..6]
    -> BB2
    -> BB32
BB2[7..8]
    -> BB3
    -> BB32
BB3[9..9]
    -> BB4
    -> BB32
BB4[10..15]
    -> BB9
    -> BB5
BB5[16..19]
    -> BB6
    -> BB32
BB6[20..22]
    -> BB7
    -> BB32
BB7[23..25]
    -> BB8
    -> BB32
BB8[26..27]
    -> BB23
BB9[28..30]
    -> BB23
    -> BB10
BB10[31..33]
    -> BB11
    -> BB32
BB11[34..34]
    -> BB13
    -> BB12
BB12[35..36]
    -> BB14
BB13[37..37]
    -> BB14
BB14[38..41]
    -> BB15
    -> BB32
BB15[42..42]
    -> BB18
    -> BB16
BB16[43..45]
    -> BB17
    -> BB32
BB17[46..49]
    -> BB19
BB18[50..50]
    -> BB19
BB19[51..51]
    -> BB23
    -> BB20
BB20[52..54]
    -> BB21
    -> BB32
BB21[55..57]
    -> BB22
    -> BB32
BB22[58..58]
    -> BB23
BB23[59..60]
    -> BB24
    -> BB32
BB24[61..62]
    -> BB25
    -> BB32
BB25[63..64]
    -> BB26
    -> BB32
BB26[65..65]
    -> BB27
    -> BB32
BB27[66..66]
    -> BB28
    -> BB32
BB28[67..67]
    -> BB29
    -> BB32
BB29[68..68]
    -> BB30
    -> BB32
BB30[69..69]
    -> BB31
    -> BB32
BB31[70..70]
    -> BB32
BB32[-1..-2]
Instructions:
BB0
BB1
0   v8 = new <JavaScriptLoader,LArray>@0     sms.js [423->1350] (line 19) {8=[arguments]}
1   v10 = global:global $$undefined          sms.js [574->604] (line 24) {10=[replaceLineBreaks]}
3   v12 = global:global $$undefined          sms.js [609->632] (line 25) {12=[androidIntent]}
5   v16 = lexical:convertPhoneToArray@L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41sms.js [518->537] (line 21)
6   check v16                                sms.js [518->537] (line 21)
BB2
7   v18 = global:global __WALA__int3rnal__globalsms.js [518->544] (line 21)
8   check v18                                sms.js [518->544] (line 21)
BB3
9   v14 = invoke v16@9 v18,v3 exception:v19  sms.js [518->544] (line 21) {14=[phone], 3=[phone]}
BB4
13   v23 = typeof(v5)                        sms.js [641->655] (line 26) {5=[options]}
14   v22 = binaryop(strict_eq) v23 , v24:#stringsms.js [641->668] (line 26)
15   conditional branch(eq) v22,v25:#0       sms.js [637->1202] (line 26)
BB5
18   v31 = global:global window              sms.js [715->721] (line 27)
19   check v31                               sms.js [715->721] (line 27)
BB6
21   v11 = prototype_values(v31)             sms.js [715->729] (line 27)
22   v29 = getfield < JavaScriptLoader, LRoot, console, <JavaScriptLoader,LRoot> > v11sms.js [715->729] (line 27) {29=[$$destructure$rcvr7]}
BB7
25   v35 = dispatch v34:#warn@25 v29,v36:#[DEPRECATED] Passing a string as a third argument is deprecated. Please refer to the documentation to pass the right parameter: https://github.com/cordova-sms/cordova-sms-plugin. exception:v37sms.js [715->916] (line 27) {34=[$$destructure$elt7], 29=[$$destructure$rcvr7]}
BB8
27   goto (from iindex= 27 to iindex = 59)   sms.js [637->1202] (line 26)
BB9
28   v39 = typeof(v5)                        sms.js [970->984] (line 30) {5=[options]}
29   v38 = binaryop(strict_eq) v39 , v40:#objectsms.js [970->997] (line 30)
30   conditional branch(eq) v38,v25:#0       sms.js [966->1202] (line 30)
BB10
32   v13 = prototype_values(v5)              sms.js [1029->1054] (line 31) {5=[options]}
33   v41 = getfield < JavaScriptLoader, LRoot, replaceLineBreaks, <JavaScriptLoader,LRoot> > v13sms.js [1029->1054] (line 31) {41=[replaceLineBreaks]}
BB11
34   conditional branch(eq) v41,v25:#0       sms.js [1029->1063] (line 31) {41=[replaceLineBreaks]}
BB12
36   goto (from iindex= 36 to iindex = 38)   sms.js [1029->1063] (line 31)
BB13
BB14
           v27 = phi  v41,v20:#false
40   v28 = prototype_values(v5)              sms.js [1077->1092] (line 32) {5=[options]}
41   v45 = getfield < JavaScriptLoader, LRoot, android, <JavaScriptLoader,LRoot> > v28sms.js [1077->1092] (line 32)
BB15
42   conditional branch(eq) v45,v25:#0       sms.js [1077->1131] (line 32)
BB16
44   v33 = prototype_values(v5)              sms.js [1103->1118] (line 32) {5=[options]}
45   v51 = getfield < JavaScriptLoader, LRoot, android, <JavaScriptLoader,LRoot> > v33sms.js [1103->1118] (line 32)
BB17
46   v50 = typeof(v51)                       sms.js [1096->1118] (line 32)
47   v49 = binaryop(strict_eq) v50 , v40:#objectsms.js [1096->1131] (line 32)
49   goto (from iindex= 49 to iindex = 51)   sms.js [1077->1131] (line 32)
BB18
BB19
           v43 = phi  v49,v45
51   conditional branch(eq) v43,v25:#0       sms.js [1073->1196] (line 32)
BB20
53   v44 = prototype_values(v5)              sms.js [1163->1178] (line 33) {5=[options]}
54   v54 = getfield < JavaScriptLoader, LRoot, android, <JavaScriptLoader,LRoot> > v44sms.js [1163->1178] (line 33)
BB21
56   v47 = prototype_values(v54)             sms.js [1163->1185] (line 33)
57   v53 = getfield < JavaScriptLoader, LRoot, intent, <JavaScriptLoader,LRoot> > v47sms.js [1163->1185] (line 33) {53=[androidIntent]}
BB22
BB23
           v48 = phi  v20:#false,v20:#false,v27,v27
           v52 = phi  v5,v21:#,v21:#,v53
59   v60 = lexical:exec@L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41sms.js [1220->1224] (line 38)
60   check v60                               sms.js [1220->1224] (line 38)
BB24
61   v61 = global:global __WALA__int3rnal__globalsms.js [1220->1347] (line 38)
62   check v61                               sms.js [1220->1347] (line 38)
BB25
63   v66 = global:global Array               sms.js [1291->1341] (line 42)
64   check v66                               sms.js [1291->1341] (line 42)
BB26
65   v64 = construct v66@65 exception:v67    sms.js [1291->1341] (line 42)
BB27
66   fieldref v64.v68:#0 = v14 = v14         sms.js [1291->1341] (line 42) {14=[phone]}
BB28
67   fieldref v64.v69:#1 = v4 = v4           sms.js [1291->1341] (line 42) {4=[message]}
BB29
68   fieldref v64.v70:#2 = v52 = v52         sms.js [1291->1341] (line 42) {52=[androidIntent]}
BB30
69   fieldref v64.v71:#3 = v48 = v48         sms.js [1291->1341] (line 42) {48=[replaceLineBreaks]}
BB31
70   v58 = invoke v60@70 v61,v6,v7,v62:#Sms,v63:#send,v64 exception:v72sms.js [1220->1347] (line 38) {6=[success], 7=[failure]}
BB32
<Code body of function L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..4]
    -> BB2
    -> BB3
BB2[5..9]
    -> BB3
BB3[-1..-2]
Instructions:
BB0
BB1
0   v1 = new <JavaScriptLoader,LArray>@0     sms.js [0->1538] (line 1) {1=[arguments]}
3   v6 = global:global cordova               sms.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
4   check v6                                 sms.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
BB2
7   v13 = global:global Function             sms.js [0->1537] (line 1)
8   v10 = construct v13@8 v12:#L./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js/./output/htmljs/assets/www/plugins/cordova-sms-plugin/www/sms.js@41 exception:v11sms.js [0->1537] (line 1)
9   v8 = dispatch v7:#define@9 v6,v9:#cordova-sms-plugin.Sms,v10 exception:v19sms.js [0->1537] (line 1) {7=[$$destructure$elt1], 6=[$$destructure$rcvr1]}
BB3
