package aQute.openapi.example.petstore.provider;

import java.util.Arrays;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.example.petstore.pet.GeneratedPet;
import aQute.openapi.example.petstore.pet.ProvideGeneratedPet;
import aQute.openapi.provider.OpenAPIBase;

@ProvideGeneratedPet
@Component(service = OpenAPIBase.class)
public class PetImpl extends GeneratedPet {

	@Reference
	PetStoreCentral central;

	@Override
	protected ApiResponse uploadFile(long petId, Optional<String> additionalMetadata, Optional<Part> file)
			throws Exception {
		ApiResponse response = new ApiResponse();

		return response;
	}

	@Override
	protected Iterable< ? extends Pet> findPetsByTags(String[] tags) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updatePet(Pet body) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub

	}

	@Override
	protected Pet getPetById(long petId) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deletePet(Optional<String> api_key, long petId) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addPet(Pet body) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected Iterable< ? extends Pet> findPetsByStatus(String[] status) throws Exception, BadRequestResponse {
		return Arrays.asList(new Pet().id(1000).name("Lucy"));
	}

	@Override
	protected void updatePetWithForm(long petId, Optional<String> name, Optional<String> status) throws Exception {
		// TODO Auto-generated method stub

	}

}
