source "amazon-ebs" "webapp-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  profile         = "dev"
  ami_users       = "${var.ami_users}"
  instance_type   = "${var.instance_type}"
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
    source      = "./packer/web-application.service"
    destination = "/tmp/web-application.service"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    script = "./packer/setup.sh"
  }
}
