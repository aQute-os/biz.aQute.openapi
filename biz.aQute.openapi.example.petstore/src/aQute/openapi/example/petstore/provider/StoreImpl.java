package aQute.openapi.example.petstore.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.example.petstore.store.GeneratedStore;
import aQute.openapi.example.petstore.store.ProvideGeneratedStore;
import aQute.openapi.provider.OpenAPIBase;

@ProvideGeneratedStore
@Component(service = OpenAPIBase.class)
public class StoreImpl extends GeneratedStore {
	@Reference
	PetStoreCentral central;

	@Override
	protected Order placeOrder(Order body) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Order getOrderById(long orderId) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deleteOrder(long orderId) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub

	}

	@Override
	protected GetInventoryResponse getInventory() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
