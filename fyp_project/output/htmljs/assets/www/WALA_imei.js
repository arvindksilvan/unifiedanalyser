<Code body of function L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@150:get>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..2]
    -> BB2
    -> BB7
BB2[3..4]
    -> BB3
    -> BB7
BB3[5..6]
    -> BB4
    -> BB7
BB4[7..7]
    -> BB5
    -> BB7
BB5[8..8]
    -> BB6
    -> BB7
BB6[9..9]
    -> BB7
BB7[-1..-2]
Instructions:
BB0
BB1
0   v6 = new <JavaScriptLoader,LArray>@0     imei.js [150->249] (line 5) {6=[arguments]}
1   v9 = lexical:exec@L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45imei.js [195->199] (line 6)
2   check v9                                 imei.js [195->199] (line 6)
BB2
3   v11 = global:global __WALA__int3rnal__globalimei.js [195->241] (line 6)
4   check v11                                imei.js [195->241] (line 6)
BB3
5   v16 = global:global Array                imei.js [231->240] (line 6)
6   check v16                                imei.js [231->240] (line 6)
BB4
7   v14 = construct v16@7 exception:v17      imei.js [231->240] (line 6)
BB5
8   fieldref v14.v18:#0 = v5 = v5            imei.js [231->240] (line 6) {5=[options]}
BB6
9   v7 = invoke v9@9 v11,v3,v4,v12:#IMEI,v13:#get,v14 exception:v19imei.js [195->241] (line 6) {3=[success], 4=[error]}
BB7
<Code body of function L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45>
CFG:
BB0[-1..-2]
    -> BB1
BB1[0..4]
    -> BB2
    -> BB7
BB2[5..5]
    -> BB3
    -> BB7
BB3[6..8]
    -> BB4
    -> BB7
BB4[9..9]
    -> BB5
    -> BB7
BB5[10..12]
    -> BB6
    -> BB7
BB6[13..13]
    -> BB7
BB7[-1..-2]
Instructions:
BB0
BB1
0   v6 = new <JavaScriptLoader,LArray>@0     imei.js [45->255] (line 1) {6=[arguments]}
1   v8 = global:global $$undefined           imei.js [82->117] (line 2)
2   lexical:exec@L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45 = v8imei.js [82->117] (line 2)
3   v12 = global:global __WALA__int3rnal__globalimei.js [93->116] (line 2)
4   check v12                                imei.js [93->116] (line 2)
BB2
5   v10 = invoke v3@5 v12,v13:#cordova/exec exception:v14imei.js [93->116] (line 2) {3=[require]}
BB3
6   lexical:exec@L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45 = v10imei.js [82->117] (line 2)
7   v17 = global:global Object               imei.js [138->252] (line 4)
8   check v17                                imei.js [138->252] (line 4)
BB4
9   v15 = construct v17@9 exception:v18      imei.js [138->252] (line 4)
BB5
10   v23 = global:global Function            imei.js [138->252] (line 4)
11   v20 = construct v23@11 v22:#L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@150:get exception:v21imei.js [138->252] (line 4)
12   fieldref v15.v19:#get = v20 = v20       imei.js [138->252] (line 4)
BB6
13   fieldref v5.v25:#exports = v15 = v15    imei.js [121->252] (line 4) {5=[module]}
BB7
<Code body of function L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js>
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
0   v1 = new <JavaScriptLoader,LArray>@0     imei.js [0->257] (line 1) {1=[arguments]}
3   v6 = global:global cordova               imei.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
4   check v6                                 imei.js [0->7] (line 1) {6=[$$destructure$rcvr1]}
BB2
7   v13 = global:global Function             imei.js [0->256] (line 1)
8   v10 = construct v13@8 v12:#L./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js/./output/htmljs/assets/www/plugins/com.plugin.imei/www/imei.js@45 exception:v11imei.js [0->256] (line 1)
9   v8 = dispatch v7:#define@9 v6,v9:#com.plugin.imei.IMEIPlugin,v10 exception:v18imei.js [0->256] (line 1) {7=[$$destructure$elt1], 6=[$$destructure$rcvr1]}
BB3
