package aQute.openapi.example.petstore.provider;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.example.petstore.user.GeneratedUser;
import aQute.openapi.example.petstore.user.ProvideGeneratedUser;
import aQute.openapi.provider.OpenAPIBase;

@Component(service = OpenAPIBase.class)
@ProvideGeneratedUser
public class UserImpl extends GeneratedUser {
	@Reference
	PetStoreCentral central;

	@Override
	protected void createUsersWithArrayInput(List<User> body) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String loginUser(String username, String password) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void logoutUser() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deleteUser(String username) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub

	}

	@Override
	protected User getUserByName(String username) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateUser(String username, User body) throws Exception, BadRequestResponse {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createUser(User body) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createUsersWithListInput(List<User> body) throws Exception {
		// TODO Auto-generated method stub

	}

}
