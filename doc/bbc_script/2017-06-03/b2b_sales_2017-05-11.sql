CREATE TABLE "t_pdd_logistics" (

"id" serial4 NOT NULL,

"logistics_id" int4,

"logistics_company" varchar(255),

PRIMARY KEY ("id") 

);

COMMENT ON TABLE "public"."t_pdd_logistics" IS '拼多多对应快递表';
COMMENT ON COLUMN "public"."t_pdd_logistics"."id" IS '主键';
COMMENT ON COLUMN "public"."t_pdd_logistics"."logistics_id" IS '快递编码';
COMMENT ON COLUMN "public"."t_pdd_logistics"."logistics_company" IS '快递公司名';

--初始化数据

INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (1, '申通快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (3, '百世快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (44, '顺丰快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (85, '圆通快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (88, '奔奔速达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (89, '赛澳递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (90, '晟邦物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (115, '中通快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (116, '全峰快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (117, '优速快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (118, '邮政EMS');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (119, '天天快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (120, '京东配送');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (121, '韵达快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (122, '快捷快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (124, '国通快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (128, '当当出版配送');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (129, '宅急送快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (130, '如风达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (131, '德邦快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (133, '龙邦快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (135, '联邦快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (136, '九曳供应链');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (137, '百城当日达快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (138, '达达快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (139, '冻到家物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (140, '南京晟邦');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (141, '山西红马甲');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (142, '万象物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (143, '立即送');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (144, '门对门');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (147, '丰程');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (148, '安达信');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (149, '海外快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (150, '飞远物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (151, '南都快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (152, '汇文快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (154, '黄马甲');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (155, '速尔快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (156, '亚马逊物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (157, '黑猫宅急便');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (158, '顺丰航运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (159, '圆通航运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (160, '拼好货');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (161, '上海赛澳递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (162, '城市100 ');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (163, '芝麻开门');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (164, '顺捷丰达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (165, '汇通小红马');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (166, '辽宁小红马');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (167, '辽宁黄马甲');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (168, '江苏赛澳递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (169, '三人行');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (170, '通和佳递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (171, '速捷');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (172, '信诺迅达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (173, '风先生');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (174, '宽容');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (175, '广州途客');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (176, '小红帽');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (177, '鹏达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (178, '福建飞远');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (179, 'E特快');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (180, '商家自建物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (181, '云鸟');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (182, '保达');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (183, '跨越速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (184, '吉林黄马甲');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (185, '城际速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (186, 'usps');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (187, '青岛安捷');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (188, '大韩通运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (189, '棒棒糖');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (190, '途鲜');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (191, '菜鸟快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (193, '汇站众享');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (194, '派客');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (195, '贝海国际速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (196, '丰泰国际快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (197, '环球速运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (198, '168顺发速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (199, '全球快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (200, '程光快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (201, '全一快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (203, '东骏快捷');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (205, '远成快运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (206, '风腾国际速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (207, '笨鸟转运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (208, '安能物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (209, '联众国际快运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (210, '天地华宇');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (211, '中邮速递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (212, 'hi淘易 ');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (213, 'EMS-国际件');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (214, '中铁物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (215, '楚源物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (216, '新邦物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (217, 'Flash Express');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (218, '新顺丰NSF');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (219, '锐朗快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (220, '王道国际物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (221, 'DCS GLOBAL');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (222, '迅速快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (223, '富腾达国际货运');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (224, '琦峰物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (225, '金运通物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (226, 'EWE全球快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (227, '日日顺物流');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (228, '苏宁快递');
INSERT INTO "t_pdd_logistics"(logistics_id,logistics_company) VALUES (132, '邮政小包');


