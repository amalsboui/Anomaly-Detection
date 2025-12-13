from mininet.topo import Topo
from mininet.net import Mininet
from mininet.cli import CLI
from mininet.link import TCLink
from mininet.node import OVSBridge

class AnomalyTopo(Topo):
    def build(self):
        s1 = self.addSwitch('s1')

        h1 = self.addHost('h1', ip='10.0.0.1/24')
        h2 = self.addHost('h2', ip='10.0.0.2/24')
        h3 = self.addHost('h3', ip='10.0.0.3/24')
        h4 = self.addHost('h4', ip='10.0.0.4/24')  # ASC

        for h in [h1, h2, h3, h4]:
            self.addLink(h, s1, cls=TCLink)

if __name__ == '__main__':
    topo = AnomalyTopo()

    net = Mininet(
        topo=topo,
        switch=OVSBridge,
        controller=None,
        link=TCLink
    )

    net.start()
    print("Topology started: h1-h3 (LocalAgents), h4 (ServerAgent)")
    CLI(net)
    net.stop()















