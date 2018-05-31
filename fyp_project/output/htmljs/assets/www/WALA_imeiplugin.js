<Code body of function L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..4]
    -> BB2
    -> BB12
BB2[5..7]
    -> BB3
    -> BB12
BB3[8..9]
    -> BB8
    -> BB4
BB4[10..11]
    -> BB5
    -> BB12
BB5[12..12]
    -> BB6
    -> BB12
BB6[13..14]
    -> BB7
    -> BB12
BB7[15..15]
    -> BB8
    -> BB12
BB8[16..16]
    -> BB9
    -> BB12
BB9[17..18]
    -> BB10
    -> BB12
BB10[19..21]
    -> BB11
    -> BB12
BB11[22..22]
    -> BB12
BB12[-1..-2]
Instructions:
BB0
BB1
0   v6 = new <JavaScriptLoader,LArray>@0     imeiplugin.js [54->332] (line 1) {6=[arguments]}
1   v10 = global:global Function             imeiplugin.js [54->332] (line 1)
2   v7 = construct v10@2 v9:#L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin exception:v8imeiplugin.js [54->332] (line 1) {7=[IMEIPlugin]}
3   v15 = global:global window               imeiplugin.js [243->249] (line 8)
4   check v15                                imeiplugin.js [243->249] (line 8)
BB2
6   v17 = prototype_values(v15)              imeiplugin.js [243->257] (line 8)
7   v13 = getfield < JavaScriptLoader, LRoot, plugins, <JavaScriptLoader,LRoot> > v17imeiplugin.js [243->257] (line 8)
BB3
8   v12 = neg v13                            imeiplugin.js [242->257] (line 8)
9   conditional branch(eq) v12,v18:#0        imeiplugin.js [239->283] (line 8)
BB4
10   v21 = global:global Object              imeiplugin.js [279->281] (line 9)
11   check v21                               imeiplugin.js [279->281] (line 9)
BB5
12   v19 = construct v21@12 exception:v22    imeiplugin.js [279->281] (line 9)
BB6
13   v23 = global:global window              imeiplugin.js [262->268] (line 9)
14   check v23                               imeiplugin.js [262->268] (line 9)
BB7
15   fieldref v23.v16:#plugins = v19 = v19   imeiplugin.js [262->281] (line 9)
BB8
16   v24 = construct v7@16 exception:v25     imeiplugin.js [313->329] (line 12) {7=[IMEIPlugin]}
BB9
17   v27 = global:global window              imeiplugin.js [285->291] (line 12)
18   check v27                               imeiplugin.js [285->291] (line 12)
BB10
20   v28 = prototype_values(v27)             imeiplugin.js [285->299] (line 12)
21   v26 = getfield < JavaScriptLoader, LRoot, plugins, <JavaScriptLoader,LRoot> > v28imeiplugin.js [285->299] (line 12)
BB11
22   fieldref v26.v29:#imeiplugin = v24 = v24imeiplugin.js [285->329] (line 12)
BB12
<Code body of function L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@131/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@189>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..0]
    -> BB2
BB2[-1..-2]
Instructions:
BB0
BB1
0   v3 = new <JavaScriptLoader,LArray>@0     imeiplugin.js [189->201] (line 4) {3=[arguments]}
BB2
<Code body of function L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..3]
    -> BB2
BB2[-1..-2]
Instructions:
BB0
BB1
0   v3 = new <JavaScriptLoader,LArray>@0     imeiplugin.js [91->237] (line 2) {3=[arguments]}
1   v7 = global:global Function              imeiplugin.js [116->235] (line 3)
2   v4 = construct v7@2 v6:#L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@131 exception:v5imeiplugin.js [116->235] (line 3)
3   fieldref v2.v10:#getImei = v4 = v4       imeiplugin.js [116->235] (line 3) {2=[this]}
BB2
<Code body of function L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js>
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
0   v1 = new <JavaScriptLoader,LArray>@0     imeiplugin.js [0->334] (line 1) {1=[arguments]}
3   v6 = global:global cordova               imeiplugin.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
4   check v6                                 imeiplugin.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
BB2
7   v13 = global:global Function             imeiplugin.js [0->333] (line 1)
8   v10 = construct v13@8 v12:#L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54 exception:v11imeiplugin.js [0->333] (line 1)
9   v8 = dispatch v7:#define@9 v6,v9:#com.joandilee.imeiplugin.imeiplugin,v10 exception:v17imeiplugin.js [0->333] (line 1) {7=[$$destructure$elt1], 6=[$$destructure$rcvr1]}
BB3
<Code body of function L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@131>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..4]
    -> BB2
    -> BB5
BB2[5..10]
    -> BB3
    -> BB5
BB3[11..11]
    -> BB4
    -> BB5
BB4[12..12]
    -> BB5
BB5[-1..-2]
Instructions:
BB0
BB1
0   v4 = new <JavaScriptLoader,LArray>@0     imeiplugin.js [131->235] (line 3) {4=[arguments]}
3   v9 = global:global cordova               imeiplugin.js [160->167] (line 4) {9=[$$destructure$rcvr2]}
4   check v9                                 imeiplugin.js [160->167] (line 4) {9=[$$destructure$rcvr2]}
BB2
7   v15 = global:global Function             imeiplugin.js [160->231] (line 4)
8   v12 = construct v15@8 v14:#L./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@54/IMEIPlugin/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@131/./output/htmljs/assets/www/plugins/com.joandilee.imeiplugin/www/imeiplugin.js@189 exception:v13imeiplugin.js [160->231] (line 4)
9   v20 = global:global Array                imeiplugin.js [228->230] (line 4)
10   check v20                               imeiplugin.js [228->230] (line 4)
BB3
11   v18 = construct v20@11 exception:v21    imeiplugin.js [228->230] (line 4)
BB4
12   v11 = dispatch v10:#exec@12 v9,v3,v12,v16:#imeiplugin,v17:#getImei,v18 exception:v22imeiplugin.js [160->231] (line 4) {10=[$$destructure$elt2], 9=[$$destructure$rcvr2], 3=[returnCallback]}
BB5
