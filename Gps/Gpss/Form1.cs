using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using GMap.NET.WindowsForms.Markers;
using GMap.NET.MapProviders;
using GMap.NET.WindowsForms;
using Parse;
using System.Diagnostics;

namespace KillingMachine
{
    public partial class Form1 : Form
    {
        Double Latitude;
        Double Longitude;

        GMapOverlay markersOverlay = new GMapOverlay();
        public Form1()
        {
            this.InitializeComponent();
            // Initialize the Parse client with your Application ID and .NET Key found on
            // your Parse dashboard
            ParseClient.Initialize("xvKAG36o6Gy4zqeACEghe5A8MiLW9Wlgq9a2EbiD", "bom1scOGsfkawE8Eim10g7PKcy1t3ge21mNMUedH");

            getData();
        }
        private void Form1_Load(object sender, EventArgs e)
        {
          
        }
        private void Form1_Load_1(object sender, EventArgs e)
        {
               
        }
        private async void getData()
        {
            var query = from tabledata in ParseObject.GetQuery("Location")
                        orderby tabledata.Get<DateTime>("createdAt")
                        select tabledata;
            double[] _latitude = new double[20000];
            double[] _longitude = new double[20000];
            int count = 0;
            await query.FindAsync().ContinueWith(t =>
            {
                IEnumerable<ParseObject> results = t.Result;
                foreach (var obj in results)
                {
                    _latitude[count] = obj.Get<double>("Latitude");
                    _longitude[count] = obj.Get<double>("Longitude");
                }
            });
           
            Latitude = _latitude[count];
            Longitude = _longitude[count];
            gMapControl1.MapProvider = GMap.NET.MapProviders.ArcGIS_World_Topo_MapProvider.Instance;
            GMap.NET.GMaps.Instance.Mode = GMap.NET.AccessMode.ServerOnly;
            gMapControl1.SetBounds(0, 0, ClientRectangle.Width, ClientRectangle.Height);
            pictureBox1.SetBounds(0, 0, ClientRectangle.Width, ClientRectangle.Height);
            gMapControl1.MinZoom = 1;
            gMapControl1.MaxZoom = 11;
            gMapControl1.Position = new GMap.NET.PointLatLng(Latitude,Longitude);
            gMapControl1.Zoom = 11;
            gMapControl1.GrayScaleMode = true;
            pictureBox1.Parent = gMapControl1;
            pictureBox2.Parent = gMapControl1;   
        }
        private void pictureBox2_Click(object sender, EventArgs e)
        {
            //Genrate a very small double too add randomess to wayout
            var r = new Random();
            double randomLat = Latitude + 0.0001 * r.Next(100);
            double randomLon = Longitude + 0.0001 * r.Next(100);
            //Drop a wayPoint marker at random location on campus
            GMarkerGoogle marker = new GMarkerGoogle(new GMap.NET.PointLatLng(randomLat,randomLon), GMarkerGoogleType.blue_dot);
            //Add wayPoint to the transparent overlay
            markersOverlay.Markers.Add(marker);
            //Add the OverLay to gmap Canvas
            gMapControl1.Overlays.Add(markersOverlay);
        }
    }
}
