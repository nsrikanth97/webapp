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

variable "user_name" {
  type    = string
  default = ""
}

variable "environment_file" {
  type    = string
  default = ""
}

source "amazon-ebs" "webapp-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  profile         = "dev"
  ami_users       = ["362731286542"]
  instance_type   = "t2.micro"
  source_ami      = "${var.source_ami}"
  ssh_username    = "${var.ssh_username}"
  subnet_id       = "${var.subnet_id}"

  aws_polling {
    delay_seconds = 30
    max_attempts  = 50
  }
  tags = {
    OS_Version    = "Debian"
    Release       = "Latest"
    Base_AMI_Name = "{{ .SourceAMIName }}"
  }
}


build {
  sources = ["source.amazon-ebs.webapp-ami"]
  provisioner "file" {
    #./target/csye6225-0.0.1-SNAPSHOT.jar
    source      = "${var.source_path_jar}"
    destination = "/tmp/csye6225-0.0.1-SNAPSHOT.jar"
  }
  provisioner "file" {
    source      = "./opt/users.csv"
    destination = "/tmp/users.csv"
  }

  provisioner "file" {
    source      = "${var.environment_file}"
    destination = "/tmp/.env"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive"
      "CHECKPOINT_DISABLE=1",
      "DB_USER=${var.user_name}"
    ]
    script = "./packer/setup.sh"
  }
}
