package component.elasticsearch;

import java.util.Arrays;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import play.Configuration;
import play.Logger;
import play.Play;

public class EsCommonUtil {
	
	private static Client client;
	
	static public Client getClient(){
		if(null == client){
			initClient();
		}
		return client;
	}
	
	/**
	 * 初始化client连接
	 * 
	 * @author ye_ziran
	 * @since 2017年1月3日 下午3:30:08
	 */
	private static void initClient(){
		Configuration esconfig = Play.application().configuration().getConfig("elasticsearch");
		Node node = null;
		if (esconfig == null) {
			Logger.info("Starting local ElasticSearch Node");
			node = NodeBuilder.nodeBuilder().local(true).build();
			node.start();
			client = node.client();
		} else {
			String clusterName = esconfig.getString("cluster_name");
			List<String> addresses = Lists.newArrayList();
			try {//允许逗号分隔
				addresses = esconfig.getStringList("server_address");
			} catch (Exception e) {
				if (esconfig.getString("server_address") != null) {
					addresses = Arrays.asList(esconfig.getString(
							"server_address").split(","));
				}
			}
			Logger.info("ElasticSearch Server Addresses: {}", addresses);

			TransportClient tclient = null;
			if (clusterName != null) {
				Settings settings = ImmutableSettings.settingsBuilder()
						.put("cluster.name", clusterName)
						.put("client.transport.sniff", true)
						.build();
				tclient = new TransportClient(settings);
				Logger.info("Connecting to Elasticsearch Node Cluster: {}",
						clusterName);
			} else {
				tclient = new TransportClient();
			}

			for (String address : addresses) {
				String[] part = address.split(":");
				if (part.length == 2) {
					tclient.addTransportAddress(new InetSocketTransportAddress(
							part[0], Integer.parseInt(part[1])));
				} else if (part.length == 1) {//默认端口，9300
					tclient.addTransportAddress(new InetSocketTransportAddress(
							part[0], 9300));
				}
			}
			client = tclient;
		}
	}

}
