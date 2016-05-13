<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Qappmodel extends CI_Model {

	function __construct()
    {
        parent::__construct();
    }

    public function insert($insert_data, $table){
    	$this->load->database();
    	$this->db->insert($table, $insert_data);
    }

    public function select($where, $table){
		$this->db->where($where);
    	$query = $this->db->get($table);
    	return $query->result_array();
    }

    public function searchevent($key){
    	$this->db->like('EventName', $key , 'both');
    	$query = $this->db->get('event');
    	return $query->result_array();
    }

    public function select_max_event_id($table,$eventname,$username){
    	$this->db->insert($table, array(
    		"EventName" => $eventname,
    		"UserID"	=> $username
    		)
    	);
    }

    public function eventlist(){
    	$this->db->limit(10);
    	$query = $this->db->get("event");
    	return $query->result_array();
    }

    public function stampprocess($where,$update){
    	$this->db->where($where);
    	$this->db->update("userline", $update);
    }


}