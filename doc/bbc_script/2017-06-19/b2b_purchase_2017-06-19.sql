ALTER TABLE "public"."t_purchase_gift_op_record"
ADD COLUMN "purchase_no" varchar(50);
update t_purchase_gift_op_record r set purchase_no=(select purchase_order_no from t_purchase_order where id=r.purchase_id);

