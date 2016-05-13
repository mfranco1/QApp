<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class H extends CI_Controller {
	public function index()
	{
		$this->load->view('welcome_message');
	}
	public function signin(){

		$username = $this->security->xss_clean( $_POST["username"] );
		$password = $this->security->xss_clean( $_POST["password"] );

		$error = 0;

		$hashed_password = md5( $password );
		$where = array(
				"UserID" 	=> $username,
				"Password" 	=> $hashed_password
			);

		$ret = $this->qappmodel->select($where, "user");

		if( ! empty( $ret[0] ) ){ $error = 1; }

		$json = array ( "status" => $error );
		echo json_encode($json);
	}

	public function signup(){

		$username 		= $this->security->xss_clean( $_POST["username"] );
		$password 		= $this->security->xss_clean( $_POST["password"] );
		$firstname 		= $this->security->xss_clean( $_POST["fname"] );
		$lastname 		= $this->security->xss_clean( $_POST["lname"] );
		$contactnumber 	= $this->security->xss_clean( $_POST["contactnumber"] );
		$error			= 0;

		// $username = "d";
		// $password = "123456789";
		// $firstname = "b";
		// $lastname = "a";
		// $contactnumber = "1111";


		// Validation
		if( 
			strlen( trim( $username ) ) == 0 || 
			strlen( trim( $password ) ) == 0 || 
			strlen( trim( $firstname ) ) == 0 || 
			strlen( trim( $lastname ) ) == 0 || 
			strlen( trim( $contactnumber ) ) == 0
			){
			$error = 1;
		}else{
			// if username is already taken
			$where = array( "UserID" => $username );
			$ret = $this->qappmodel->select($where, "user");
			if( ! empty( $ret[0] ) ){ $error = 2; }
			else{

				$hashed_password = md5($password);
				$insert_data = array(
					"UserID" 	=> $username,
					"Password" 	=> $hashed_password,
					"LastName" 	=> $lastname,
					"FirstName" => $firstname,
					);

				$this->qappmodel->insert($insert_data, "user");
			}
		}

		$json = array ( 
			"status" => $error,
			"info" => array(
				"username"		=> $username,
				"firstname"		=> $firstname,
				"lastname"		=> $lastname,
				"contactnumber"	=> $contactnumber
				)
			);

		echo json_encode($json);
	}

	function searcheventt(){

		$key = $this->security->xss_clean( $_POST["key"] );
		// $key = "port";
		$status = 0;
		// Validation

		if ( strlen( trim( $key ) ) == 0 ){ $status = 1; }
		else { $result = $this->qappmodel->searchevent($key); }

		$json = array( 
			"status" => $status,
			"result" => $result
			);

		echo json_encode($json);

	}

	public function searchevent(){

		$result = $this->qappmodel->eventlist();
		$json = array( 
			"result" => $result
			);

		echo json_encode($json);

	}

	public function createevent(){

		// $username 		= $this->security->xss_clean( $_POST["username"] );
		// $eventname 	= $this->security->xss_clean( $_POST["eventname"] );
		$username		= "kviray";
		$eventname		= "Licensing";

		$this->qappmodel->select_max_event_id("event", $eventname, $username);

	}

	public function managemyevent(){
		$username 	= $this->security->xss_clean( $_POST["username"] );

		$where  	= array("UserID" => $username);
		$result 	= $this->qappmodel->select($where, "event");

		$json 		= array( "myevents" => $result );
		echo json_encode($json);

	}

	public function myevent(){

		$eventno 	= $this->security->xss_clean( $_POST["eventno"] );
		// $eventno 	= 1;

		$where 		= array("EventID" => $eventno);
		$result 	= $this->qappmodel->select($where, "userline");

		$json 		= array("peoplejoined" => $result);
		echo json_encode($json);

	}

	public function processed(){

		$username 	= $this->security->xss_clean( $_POST["username"] );
		$eventid 	= $this->security->xss_clean( $_POST["eventid"] );
		// $username	= "kviray";
		// $eventid 	= "1";

		$where = array(
				"UserID"	=> $username,
				"EventID"	=> $eventid
			);

		$update_data = array("Processed" => 1);

		$this->qappmodel->stampprocess($where,$update_data);

	}

	public function event(){
		$event = $this->security->xss_clean( $_POST["event"] );
		// $event 		= 1;

		$where 		= array("EventID" => $event);
		$result 	= $this->qappmodel->select($where, "event");

		$json 		= array("event" => $result);
		echo json_encode($json);

	}

	public function joinevent(){

		$username 		= $this->security->xss_clean( $_POST["username"] );
		$eventno 		= $this->security->xss_clean( $_POST["eventid"] );
		$datetime 		= date("YmdHi");

		$insert_data = array(
			"EventID"			=> $eventno,
			"UserID"			=> $username,
			"DateTimeJoined"	=>$datetime
			);

		$this->qappmodel->insert($insert_data,'userline');
		$json 		= array("status" => "0");
		echo json_encode($json);
	}
}
