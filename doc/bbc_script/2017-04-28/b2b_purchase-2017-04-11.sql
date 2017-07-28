alter table t_return_order_detail
ADD COLUMN "is_gift" bool;
COMMENT ON COLUMN "public"."t_return_order_detail"."is_gift" IS '是否是赠品（true赠品；false正价商品）';

update t_return_order_detail set is_gift = false;