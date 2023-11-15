packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Debian 22.04 LTS
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-00497d9f1e55f8674"
}
variable "source_path_jar" {
  type    = string
  default = ""
}

variable "ami_users" {
  type    = list(string)
  default = ["362731286542", "921922858617", "017960186760"]
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "aws_profile" {
  type    = string
  default = "dev"
}
